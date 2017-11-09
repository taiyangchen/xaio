/**
 * 
 */
package com.sm.common.xaio;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.NetworkChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sm.common.libs.able.Bootstrap;
import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.bo.PeerInfo;

/**
 * 异步传输通道支持类
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月12日 下午5:42:50
 */
public abstract class AsyncChannelSupport<E> extends LoggerSupport implements Bootstrap {

  // protected long waitTimeout;

  /**
   * 发送超时时间，毫秒
   */
  protected int sendTimeout; // ms

  /**
   * 启动时间
   */
  protected long startTime;

  /**
   * 请求接收器
   */
  protected Receiver<E> receiver;

  // protected int messageCached = 1024;

  /**
   * 编解码器
   */
  protected MessageCodec codec;

  /**
   * 对等实体
   */
  protected PeerInfo server = new PeerInfo();

  /**
   * 绑定到<code>AsynchronousChannelGroup</code>上的线程数
   */
  protected int threads = Runtime.getRuntime().availableProcessors();

  protected AsynchronousChannelGroup channelGroup;

  /**
   * 会话管理器
   */
  protected SessionManager<E> sessionManager;

  /**
   * 发送缓冲区
   */
  protected int sendBuffer;

  /**
   * 接收缓冲区
   */
  protected int receiveBuffer;

  protected int pendings = 1024;

  /**
   * 传输通道设置参数
   * <p>
   * FIXME buffer backlog 1024 ? server
   * 
   * @param channel 传输通道 @see NetworkChannel
   * @throws IOException
   */
  protected void setOptions(NetworkChannel channel) throws IOException {
    channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
    channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
    if (sendBuffer > 0) {
      channel.setOption(StandardSocketOptions.SO_SNDBUF, sendBuffer);
    }
    if (receiveBuffer > 0) {
      channel.setOption(StandardSocketOptions.SO_RCVBUF, receiveBuffer);
    }
  }

  /**
   * 创建传输通道上下文
   * 
   * @param channel 传输通道
   * @return 传输通道上下文 @see ChannelContext
   */
  protected abstract ChannelContext<E> createChannelContext(AsynchronousSocketChannel channel);

  /**
   * 创建新的会话
   * 
   * @param channelContext 传输通道上下文 @see ChannelContext
   * @return 新的会话 @see Session
   */
  protected abstract Session<E> newSession(ChannelContext<E> channelContext);

  /**
   * 创建新的会话
   * 
   * @param channel 传输通道上下文 @see ChannelContext
   * @param type 会话类型 @see {@link #Session.Type}
   * @return 新的会话 @see Session
   */
  protected Session<E> newSession(AsynchronousSocketChannel channel, Session.Type type) {
    ChannelContext<E> context = createChannelContext(channel);
    BlockingQueue<E> queue = new LinkedBlockingQueue<>(pendings);
    context.setPendings(queue);
    context.codec(codec);
    Session<E> session = newSession(context);
    session.setType(type);
    // session.setQueueSize(messageCached);
    session.setReceiver(receiver);
    if (sendTimeout > 0) {
      session.setSendTimeout(sendTimeout);
    }
    context.session(session);
    // if (waitTimeout > 0) {
    // session.setWaitTimeout(waitTimeout);
    // }
    // FIXME
    context.sessionManager(sessionManager);
    return session;
  }

  /**
   * 关闭<code>AsynchronousChannelGroup</code>
   * 
   * @param channelGroup
   * @throws IOException
   */
  protected void shutdown(AsynchronousChannelGroup channelGroup) throws IOException {
    if (channelGroup == null || channelGroup.isShutdown()) {
      return;
    }

    channelGroup.shutdown();
    try {
      if (!channelGroup.awaitTermination(1, TimeUnit.MINUTES)) { // 等待一分钟
        channelGroup.shutdownNow();
      }
    } catch (InterruptedException ie) {
      channelGroup.shutdownNow();
      Thread.currentThread().interrupt();
    }
    channelGroup.shutdownNow();
  }

  public PeerInfo getPeer() {
    return server;
  }

  public void setSendTimeout(int sendTimeout) {
    this.sendTimeout = sendTimeout;
  }

  // public void setWaitTimeout(long waitTimeout) {
  // this.waitTimeout = waitTimeout;
  // }

  public void setReceiver(Receiver<E> receiver) {
    this.receiver = receiver;
  }

  // public void setMessageCached(int messageCached) {
  // this.messageCached = messageCached;
  // }

  public void setCodec(MessageCodec codec) {
    this.codec = codec;
  }

  public void setHostName(String hostName) {
    server.setHostName(hostName);
  }

  public void setPort(int port) {
    server.setPort(port);
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setSendBuffer(int sendBuffer) {
    this.sendBuffer = sendBuffer;
  }

  public void setReceiveBuffer(int receiveBuffer) {
    this.receiveBuffer = receiveBuffer;
  }

  public void setPendings(int pendings) {
    this.pendings = pendings;
  }

}
