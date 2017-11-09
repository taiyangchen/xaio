/**
 * 
 */
package com.sm.common.xaio.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.sm.common.libs.core.SimpleThreadFactory;
import com.sm.common.libs.exception.MessageRuntimeException;
import com.sm.common.xaio.Client;
import com.sm.common.xaio.HostType;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.bo.PeerInfo;
import com.sm.common.xaio.bo.ServerInfo;
import com.sm.common.xaio.client.DefaultAsyncClient;

/**
 * DefaultRouter
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 上午2:15:28
 */
public class DefaultClientRouter extends ClusterSupport<Message<Integer, Object>> {

  private ScheduledExecutorService scheduler;

  private long heartbeatInterval = 30 * 1000;

  private List<PeerInfo> originalPeers = new ArrayList<>();

  @Override
  public void start() throws Exception {
    for (PeerInfo peer : originalPeers) {
      Client<Message<Integer, Object>> client = create(peer);
      clientMap.putIfAbsent(peer, client);
    }
    snapshot = new ArrayList<>(clientMap.keySet());
    updateRoutes();

    super.start();
    startHeartbeat();
  }

  private void startHeartbeat() {
    scheduler = Executors.newSingleThreadScheduledExecutor(
        new SimpleThreadFactory(this.getClass().getSimpleName() + "-singleScheduler"));

    scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        // check hosts
        List<PeerInfo> alived = new ArrayList<>();
        for (PeerInfo peer : originalPeers) {
          Client<Message<Integer, Object>> client = clientMap.get(peer);
          if (client != null && client.isActive()) {
            alived.add(peer);
          }
        }

        refreshRoute(alived);
      }
    }, 0, heartbeatInterval, TimeUnit.MILLISECONDS);
  }

  @Override
  public void stop() throws Exception {
    if (scheduler != null) {
      scheduler.shutdownNow();
    }

    super.stop();
  }

  @Override
  protected Client<Message<Integer, Object>> createClient() {
    DefaultAsyncClient client = new DefaultAsyncClient(true);
    client.setCodec(codec);
    client.setReceiver(receiver);
    return client;
  }

  // FIXME use IpPortUtil ?
  public void setHosts(String hosts) {
    String[] hostArray = StringUtils.split(hosts, "/,");

    for (String ipPort : hostArray) {
      if (ipPort == null) {
        break;
      }

      String[] servers = ipPort.split(":");
      ServerInfo serverInfo = HostType.toServerInfo(servers);
      if (serverInfo == null) {
        throw new MessageRuntimeException("host [{}] not match IP:PORT", ipPort);
      }

      PeerInfo peer = serverInfo.getPeerInfo();
      originalPeers.add(peer);
    }
  }

}
