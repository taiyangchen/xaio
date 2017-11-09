/**
 * 
 */
package com.sm.common.xaio.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.core.SimpleThreadFactory;
import com.sm.common.xaio.Client;
import com.sm.common.xaio.TransportException;
import com.sm.common.xaio.bo.PeerInfo;
import com.sm.common.xaio.bo.ServerCategory;
import com.sm.common.xaio.route.RoundRobin;
import com.sm.common.xaio.route.Scheduling;
import com.sm.common.xaio.route.SchedulingStrategy;

/**
 * 代表一个持有多连接的客户端
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月27日 上午11:35:45
 */
public abstract class ClusterSupport<E> extends LoggerSupport implements PeerSendCluster<E> {

  protected Scheduling scheduling = new RoundRobin();

  protected AtomicLong freshStamp = new AtomicLong(0);

  // FIXME
  protected AtomicReference<PeerInfo[]> routesRef = new AtomicReference<PeerInfo[]>(new PeerInfo[0]);

  protected ConcurrentMap<PeerInfo, Client<E>> clientMap = new ConcurrentHashMap<>();

  // FIXME use set?
  protected List<PeerInfo> snapshot = new ArrayList<>();

  protected String version = "default";

  protected ServerCategory category = new ServerCategory();

  protected ExecutorService callbackExecutor;

  protected ClusterEventListener<E> listener;

  protected MessageCodec codec;

  protected Receiver<E> receiver;

  protected int maxSessions = 2;

  @Override
  public boolean isActive() {
    for (Client<E> client : clientMap.values()) {
      if (client.isActive()) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void start() throws Exception {
    callbackExecutor = Executors.newSingleThreadScheduledExecutor(
        new SimpleThreadFactory(this.getClass().getSimpleName() + "-single-eventCallback"));
    routesRef.set(snapshot.toArray(new PeerInfo[0]));
    scheduling.setTotal(routesRef.get().length);
    // FIXME 启动时不检查是否存活，因为一般都正常
    for (Client<E> client : clientMap.values()) {
      client.start();
    }
  }

  @Override
  public void stop() throws Exception {
    if (callbackExecutor != null) {
      callbackExecutor.shutdownNow();
    }

    for (Map.Entry<PeerInfo, Client<E>> entry : clientMap.entrySet()) {
      PeerInfo key = entry.getKey();
      Client<E> client = entry.getValue();
      if (client == null) {
        continue;
      }
      client.stop();
      logger.warn("connector ([{}]) isClosed", client.getPeer().getName());
      remove(key);
    }

    updateRoutes();
  }

  protected void recreate(List<PeerInfo> peers) {
    // 删除无效连接
    for (PeerInfo key : clientMap.keySet()) {
      if (peers.contains(key)) {
        continue;
      }
      Client<E> out = clientMap.remove(key);
      if (null != out) {
        try {
          out.stop();
        } catch (Exception e) {
          logger.error("stop connector = " + out.getPeer() + " error", e);
        }
      }
    }
    // joins
    for (PeerInfo info : peers) {
      join(info);
    }
  }

  @Override
  public void send(Object bean) {
    Client<E> client = next();
    if (client != null) {
      client.send(bean);
      return;
    }

    logger.warn("send: no route, msg [{}] lost. route=[{}]", bean, category);
  }

  @Override
  public <T> void send(ResponseCallback<T> callback, Object bean) {
    Client<E> client = next();
    if (client != null) {
      client.send(callback, bean);
      return;
    }

    logger.warn("send: no route, msg [{}] lost. route=[{}]", bean, category);
  }

  @Override
  public <T> T sendAndWait(Object bean) throws TransportException {
    Client<E> client = next();
    if (client != null) {
      return client.sendAndWait(bean);
    }

    logger.warn("send: no route, msg [{}] lost. route=[{}]", bean, category);
    return null;
  }

  @Override
  public <T> T sendAndWait(Object bean, long duration, TimeUnit unit) throws TransportException {
    Client<E> client = next();
    if (client != null) {
      return client.sendAndWait(bean, duration, unit);
    }

    logger.warn("send: no route, msg [{}] lost. route=[{}]", bean, category);
    return null;
  }

  @Override
  public Client<E> next() {
    int index = scheduling.next();
    PeerInfo info = routesRef.get()[index];
    Client<E> client = clientMap.get(info);
    if (client != null && client.isActive()) {
      return client;
    }

    // client = join(info);
    // if (client != null && client.isActive()) {
    // return client;
    // }

    // this.doRefreshRoute(snapshot);
    for (int tryTimes = 0, size = routesRef.get().length; tryTimes < size; tryTimes++) {
      client = schedule(tryTimes);
      if (client != null && client.isActive()) {
        return client;
      }
    }

    return null;
  }

  private Client<E> schedule(int index) {
    PeerInfo info = routesRef.get()[index];
    return clientMap.get(info);
  }

  private Client<E> resetClient(PeerInfo peer) {
    Client<E> client = clientMap.get(peer);
    // 正常
    if (client != null && client.isActive()) {
      return client;
    }
    // 不存在
    if (client == null) {
      return create(peer);
    }
    // 无法连通
    try {
      client.stop();
      return client;
    } catch (Exception e) {
      logger.error("stop client error,to create new client", e);
      return create(peer);
    }

  }

  // FIXME
  @Override
  public Client<E> join(PeerInfo peer) {
    Client<E> client = resetClient(peer);
    // 简单处理下防并发
    Client<E> oldClient = clientMap.putIfAbsent(peer, client);
    if (oldClient != null && oldClient.isActive()) {
      logger.info("peer=[{}] already joined ", peer);
      return oldClient;
    }
    Client<E> newClient;
    try {
      newClient = startAndGet(oldClient, client);
    } catch (Exception e) {
      logger.error("create connector = " + client.getPeer() + " error", e);
      clientMap.remove(peer);
      return null;
    }

    if (!newClient.isActive()) {
      snapshot.remove(peer);
      updateRoutes();
      return null;
    }
    if (!snapshot.contains(peer)) {
      snapshot.add(peer);
    }
    updateRoutes();
    executeJoin(client);
    logger.info("peer=[{}] joining success ", peer);
    return client;
  }

  private Client<E> startAndGet(Client<E> oldClient, Client<E> client) throws Exception {
    if (oldClient != null) {
      oldClient.stop();
      oldClient.start();
      if (!oldClient.isActive()) {
        oldClient.stop();
      }
      return oldClient;
    }

    client.start();
    if (!client.isActive()) {
      client.stop();
    }
    return client;
  }

  protected Client<E> create(PeerInfo peer) {
    Client<E> client = createClient();
    client.setHostName(peer.getHostName());
    client.setPort(peer.getPort());
    client.setMaxSessions(maxSessions);

    return client;
  }

  private void executeJoin(final Client<E> client) {
    if (listener != null) {
      callbackExecutor.execute(new Runnable() {
        @Override
        public void run() {
          listener.joinCompleted(client);
        }
      });
    }
  }

  protected abstract Client<E> createClient();

  protected void updateRoutes() {
    Collections.sort(snapshot);
    routesRef.set(snapshot.toArray(new PeerInfo[0]));
    scheduling.setTotal(routesRef.get().length);
  }

  // FIMXE not used
  @Override
  public boolean leave(PeerInfo peer) {
    Client<E> client = clientMap.get(peer);
    if (client == null) {
      logger.info("peer=[{}] not exist", client);
      return false;
    }

    try {
      client.stop();
    } catch (Exception e) {
      logger.error("stop connector = " + client.getPeer() + " error", e);
    }

    remove(peer);
    updateRoutes();
    executeLeave(peer);

    return true;
  }

  private void executeLeave(final PeerInfo peer) {
    if (listener != null) {
      callbackExecutor.execute(new Runnable() {
        @Override
        public void run() {
          listener.leaveCompleted(peer);
        }
      });
    }
  }

  protected void remove(PeerInfo peer) {
    clientMap.remove(peer);
    snapshot.remove(peer);
  }

  // 先不考虑权重
  public synchronized void refreshRoute(List<PeerInfo> peers) {
    Collections.sort(peers);
    if (!snapshot.equals(peers)) {
      freshStamp.compareAndSet(freshStamp.get(), freshStamp.incrementAndGet());

      logger.info("refreshRoute [{}]: update routes info:[{}]/lastRoutes:[{}].",
          new Object[] {category, peers, snapshot});
      // FIXME 先这样
      snapshot.clear();
      snapshot.addAll(peers);
      recreate(peers);
    }

  }

  @Override
  public void registerListener(ClusterEventListener<E> listener) {
    this.listener = listener;
  }

  @Override
  public int size() {
    return clientMap.size();
  }

  public Map<PeerInfo, Integer> sessionMap() {
    Map<PeerInfo, Client<E>> copy = new HashMap<>(clientMap);
    Map<PeerInfo, Integer> ret = new HashMap<>(snapshot.size());
    for (Map.Entry<PeerInfo, Client<E>> entry : copy.entrySet()) {
      Client<E> client = entry.getValue();
      ret.put(entry.getKey(), client.sessionSize());
    }

    return ret;
  }

  public void setScheduling(Scheduling scheduling) {
    this.scheduling = scheduling;
  }

  public void setSchedulingStrategy(SchedulingStrategy strategy) {
    this.scheduling = strategy.getScheduling();
  }

  public void setVersion(String version) {
    this.version = version;
  }

  // FIXME group@domain
  public void setCategory(String category) {
    this.category.setCategory(category);
  }

  public void setCodec(MessageCodec codec) {
    this.codec = codec;
  }

  public void setReceiver(Receiver<E> receiver) {
    this.receiver = receiver;
  }

  public void setMaxSessions(int maxSessions) {
    this.maxSessions = maxSessions;
  }

}
