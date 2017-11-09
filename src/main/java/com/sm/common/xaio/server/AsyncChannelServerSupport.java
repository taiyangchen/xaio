/**
 * 
 */
package com.sm.common.xaio.server;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.sm.common.libs.core.SimpleThreadFactory;
import com.sm.common.libs.util.IOUtil;
import com.sm.common.xaio.AsyncChannelSupport;
import com.sm.common.xaio.AsyncServer;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.session.DefaultSessionManager;

/**
 * 异步传输通道服务端支持类
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月12日 下午5:42:50
 */
public abstract class AsyncChannelServerSupport<E> extends AsyncChannelSupport<E> implements AsyncServer {

  /**
   * 重试上限
   */
  protected static final int maxRetry = 20;

  /**
   * 默认超时重试时间
   */
  protected long retryTimeout = 30 * 1000; // 30s

  /**
   * 服务端异步传输通道
   */
  protected AsynchronousServerSocketChannel serverChannel;

  /**
   * 最大会话上限
   */
  protected int maxSession = 2000;

  @Override
  public void start() throws Exception {
    init();
    bind();

    logger.info("start succeed in {}:{}", server.getHostName(), server.getPort());
    startTime = System.currentTimeMillis();

    serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, AsynchronousSocketChannel>() {
      @Override
      public void completed(AsynchronousSocketChannel channel, AsynchronousSocketChannel attachment) {
        if (sessionManager.isFull()) {
          logger.warn("doConnect: reach max session: {}, cancel this action.", maxSession);
          return;
        }
        serverChannel.accept(channel, this);
        try {
          // FIXME
          setOptions(channel);
          listenReadable(channel);
        } catch (Exception e) {
          logger.error(server + " acceptor error", e);
          IOUtil.close(channel);
        }
      }

      @Override
      public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
        logger.error(server + " acceptor failed", exc);

        IOUtil.close(attachment);
      }
    });
  }

  /**
   * 传输通道监听读事件
   * 
   * @param channel 异步传输通道
   * @throws IOException
   */
  private void listenReadable(AsynchronousSocketChannel channel) throws IOException {
    // FIXME
    setOptions(channel);
    logger.info("create connect {} <--> [{}]", channel.getRemoteAddress(), channel.getLocalAddress());

    Session<E> session = newSession(channel, Session.Type.SERVER);
    session.start();
    sessionManager.addSession(session);
  }

  private void bind() throws Exception {
    int retryCount = 0;
    boolean binded = false;
    do {
      try {
        serverChannel.bind(server.getAddress());
        binded = true;
      } catch (IOException e) {
        logger.warn("start failed : " + e + ", and retry...");
        // 对绑定异常再次进行尝试
        retryCount++;
        if (retryCount >= maxRetry) {
          // 超过最大尝试次数
          throw e;
        }
        Thread.sleep(retryTimeout);
      }
    } while (!binded);

  }

  private void init() throws IOException {
    this.channelGroup = AsynchronousChannelGroup.withFixedThreadPool(threads,
        SimpleThreadFactory.create(DefaultAsyncServer.class.getSimpleName()));
    serverChannel = AsynchronousServerSocketChannel.open(channelGroup);
    serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    if (sendBuffer > 0) {
      serverChannel.setOption(StandardSocketOptions.SO_SNDBUF, sendBuffer);
    }
    if (receiveBuffer > 0) {
      serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, receiveBuffer);
    }

    sessionManager = new DefaultSessionManager<E>(maxSession);
  }

  @Override
  public synchronized void stop() throws Exception {
    sessionManager.stop();
    if (serverChannel == null) {
      return;
    }

    try {
      serverChannel.close();
    } catch (Exception e) {
      logger.error("stop error ", e);
    }

    this.shutdown(channelGroup);
    logger.info("server [/{}:{}] stopped", server.getHostName(), server.getPort());

  }

  @Override
  public boolean isActive() {
    return serverChannel != null && serverChannel.isOpen();
  }

  @Override
  public void setMaxSessions(int maxSession) {
    this.maxSession = maxSession;
  }

}
