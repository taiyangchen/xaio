/**
 * 
 */
package com.sm.common.xaio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.core.SimpleThreadFactory;
import com.sm.common.libs.util.CollectionUtil;
import com.sm.common.libs.util.DateUtil;
import com.sm.common.libs.util.ExecutorUtil;
import com.sm.common.libs.util.IOUtil;
import com.sm.common.xaio.AsyncChannelSupport;
import com.sm.common.xaio.AsyncClient;
import com.sm.common.xaio.Keepalive;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.TransportException;
import com.sm.common.xaio.session.DefaultSessionManager;

/**
 * 异步传输通道客户端支持类，直接通过客户端发送信息会依次轮训所有<code>Session</code><br>
 * 当且仅当<code>threads==1</code>时与通过<code>Session</code>发送效果一致
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月12日 下午5:42:50
 */
public abstract class AsyncChannelClientSupport<E> extends AsyncChannelSupport<E> implements AsyncClient<E> {

  /**
   * 重连时间
   */
  protected long reconnectTimeout = 10; // s

  protected boolean autoCreateSession;

  private Keepalive<E> keepalive = new DefaultKeepalive();

  protected int maxSessions = 2;

  /**
   * 连接服务端
   */
  private AsynchronousSocketChannel connect() {
    InetSocketAddress address = server.getAddress();
    logger.info("start connect {}:{}", address.getAddress().getHostAddress(), address.getPort());
    AsynchronousSocketChannel channel = null;
    try {
      channel = AsynchronousSocketChannel.open(channelGroup);
      setOptions(channel);
      channel.connect(address).get(reconnectTimeout, TimeUnit.SECONDS);
      logger.info("create connect {} <--> [{}]", channel.getLocalAddress(), channel.getRemoteAddress());
      return channel;
    } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
      if (channel != null) {
        IOUtil.close(channel);
      }
      // FIXME error or warn ?
      logger.warn("connect {} failed,message=[{}]", address, e.getMessage());
      return null;
    }
  }

  @Override
  public synchronized void start() throws Exception {
    if (channelGroup != null) {
      channelGroup.shutdownNow();
    }
    int sessionNumber = maxSessions > 0 ? maxSessions : threads;
    startTime = System.currentTimeMillis();
    String groupName = this.getClass().getSimpleName() + " connected " + server + " create at "
        + DateUtil.getFullChinesePattern(new Date(startTime));
    this.channelGroup =
        AsynchronousChannelGroup.withFixedThreadPool(sessionNumber, SimpleThreadFactory.create(groupName));
    sessionManager = new DefaultSessionManager<E>(sessionNumber);
    if (!autoCreateSession) {
      return;
    }

    startSessions(sessionNumber);
  }

  private void startSessions(int sessionNumber) throws IOException {
    for (int i = 0; i < sessionNumber; i++) {
      AsynchronousSocketChannel channel = connect();
      if (channel == null) {
        logger.warn("channel not exist,continue");
        continue;
      }

      Session<E> session = newSession(channel, Session.Type.CLIENT);
      if (reconnectTimeout > 0) {
        keepalive.addSession(session);
      }

      session.start();
      sessionManager.addSession(session);
      logger.info("create session=[{}] success!", session);
    }

    if (CollectionUtil.isNotEmpty(sessionManager.getSessions())) {
      keepalive.start();
    }

    if (CollectionUtil.isEmpty(sessionManager.getSessions()) && channelGroup != null) {
      logger.warn("no session created,to shutdown channel group");
      channelGroup.shutdownNow();
    }
  }

  @Override
  public synchronized void stop() throws Exception {
    keepalive.stop();
    sessionManager.stop();
    // FIXME
    if (channelGroup != null && !channelGroup.isShutdown()) {
      channelGroup.shutdownNow();
    }

    // this.shutdown(channelGroup);
    logger.info("client to [/{}:{}] stopped", server.getHostName(), server.getPort());
  }

  @Override
  public boolean isActive() {
    return sessionManager != null && !sessionManager.getSessions().isEmpty() && sessionManager.getSession().isActive();
  }

  @Override
  public Session<E> getSession() {
    if (sessionManager.isFull()) {
      return sessionManager.getSession();
    }

    AsynchronousSocketChannel channel = connect();
    if (channel == null) {
      return null;
    }

    Session<E> session = newSession(channel, Session.Type.CLIENT);
    if (reconnectTimeout > 0) {
      keepalive.addSession(session);
    }

    session.start();
    sessionManager.addSession(session);
    logger.info("create session id=[{}] success!", session.getId());
    return session;
  }

  @Override
  public void send(Object bean) {
    getOrCreateSession().send(bean);
  }

  @Override
  public <T> void send(ResponseCallback<T> callback, Object bean) {
    getOrCreateSession().send(callback, bean);
  }

  @Override
  public <T> T sendAndWait(Object bean) throws TransportException {
    return getOrCreateSession().sendAndWait(bean);
  }

  @Override
  public <T> T sendAndWait(Object bean, long duration, TimeUnit unit) throws TransportException {
    return getOrCreateSession().sendAndWait(bean, duration, unit);
  }

  private Session<E> getOrCreateSession() {
    Session<E> session = sessionManager.getSession();
    if (session != null) {
      return session;
    }

    return getSession();
  }

  @Override
  public int sessionSize() {
    return sessionManager.size();
  }

  public void setReconnectTimeout(long reconnectTimeout) {
    this.reconnectTimeout = reconnectTimeout;
  }

  public void setAutoCreateSession(boolean autoCreateSession) {
    this.autoCreateSession = autoCreateSession;
  }

  public void setKeepalive(Keepalive<E> keepalive) {
    this.keepalive = keepalive;
  }

  /**
   * 默认的连接保持
   * 
   * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
   * @version create on 2016年11月27日 下午10:08:08
   */
  private class DefaultKeepalive extends LoggerSupport implements Keepalive<E> {

    private List<Session<E>> sessions = new ArrayList<>();

    /**
     * 10分钟
     */
    private int tries = 6 * 10;

    private int tryNum;

    DefaultKeepalive() {

    }

    /**
     * 重连调度器
     */
    private ScheduledExecutorService scheduler;

    @Override
    public boolean isActive() {
      return scheduler != null && !scheduler.isShutdown();
    }

    @Override
    public void start() {
      scheduler =
          Executors.newSingleThreadScheduledExecutor(new SimpleThreadFactory(DefaultKeepalive.class.getSimpleName()));
      scheduler.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
          checkConnect();
        }
      }, 0, reconnectTimeout, TimeUnit.SECONDS);
    }

    /**
     * 连接服务端 FIXME使用bus通知？
     */
    private void checkConnect() {
      for (Session<E> session : sessions) {
        if (session.isActive()) {
          long now = System.currentTimeMillis();
          logger.debug("connect already created session={} , keepalive in {}ms", session, now - startTime);
          continue;
        }

        try {
          // 重置channel
          AsynchronousSocketChannel channel = connect();
          if (channel == null) {
            // 直接跳出此次循环
            if (++tryNum >= tries) {
              AsyncChannelClientSupport.this.stop();
            }

            break;
          }

          session.resetChannel(channel);
          logger.info("reset session=[{}] success!", session);
          startTime = System.currentTimeMillis();

        } catch (Exception e) {
          logger.error("checkConnect error", e);
        }
      }

    }

    @Override
    public void stop() {
      // channel.close();
      if (!ExecutorUtil.isShutDown(scheduler)) {
        scheduler.shutdownNow();
        logger.warn("stop the keepalive to server : {}", server);
      }
    }

    @Override
    public void addSession(Session<E> session) {
      sessions.add(session);
    }

    public void setTries(int tries) {
      this.tries = tries;
    }

  }

  public void setTries(int tries) {
    keepalive.setTries(tries);
  }

  @Override
  public void setMaxSessions(int maxSessions) {
    this.maxSessions = maxSessions;
  }

}
