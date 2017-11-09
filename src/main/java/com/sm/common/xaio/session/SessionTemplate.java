/**
 * 
 */
package com.sm.common.xaio.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.able.Sender;
import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.bus.BusSignalListener;
import com.sm.common.libs.bus.DefaultBusRegistry;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.util.MessageUtil;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.ConnectedInfo;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.SignalObject;
import com.sm.common.xaio.TransportException;
import com.sm.common.xaio.receiver.DebugReceiver;

/**
 * 会话实现的抽象类
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月24日 上午1:30:50
 */
public abstract class SessionTemplate<E> extends LoggerSupport
    implements Session<E>, BusSignalListener<SignalObject<E>> {

  /**
   * 会话ID生成器
   */
  protected static final AtomicInteger sessionUid = new AtomicInteger();

  /**
   * 传输通道上下文 @see ChannelContext
   */
  protected ChannelContext<E> channelContext;

  /**
   * 请求发送的等待时间，单位毫秒 10000
   */
  protected int sendTimeout = 10000; // ms

  /**
   * 消息接收器
   */
  protected Receiver<E> receiver = new DebugReceiver<>();

  /**
   * <code>ResponseFuture</code>上下文
   */
  // protected final Map<Integer, Object> futureContext = new HashMap<>();
  protected final Map<Integer, Object> futureContext = new ConcurrentHashMap<>();

  /**
   * 会话ID
   */
  protected int id;

  /**
   * 消息发送器
   */
  protected Sender<E> sender;

  /**
   * 会话类型，客户端或服务端 @see Type
   */
  protected Type type;

  protected ConnectedInfo connectedInfo;

  protected BusRegistry<SignalObject<E>> busRegistry = new DefaultBusRegistry<>();

  protected Thread thread;

  // /**
  // * 会话是否broke
  // */
  // protected volatile boolean broke;

  public SessionTemplate(ChannelContext<E> channelContext) {
    this.channelContext = channelContext;
    initConnectedInfo();
    id = sessionUid.incrementAndGet();
    busRegistry.getSignalManager().bind(SignalObject.class, this);
    thread = Thread.currentThread();
  }

  private void initConnectedInfo() {
    connectedInfo = new ConnectedInfo();
    try {
      connectedInfo.setLocalAddress(channelContext.channel().getLocalAddress());
      connectedInfo.setRemoteAddress(channelContext.channel().getRemoteAddress());
      // connectedInfo.setPid(JmxUtil.getPid());
    } catch (IOException e) {
      logger.error("initConnectedInfo error", e);
    }
  }

  /**
   * 初始化发送处理器
   */
  protected abstract void initSendHandler();

  /**
   * 初始化接收处理器
   * 
   * @param buffer 初始化的接收区缓冲
   */
  protected abstract void initReceiveHandler(ByteBuffer buffer);

  @Override
  public void start() {
    initSendHandler();
    ByteBuffer buffer = ByteBuffer.allocate(channelContext.codec().getHeader().getHeaderSize());
    initReceiveHandler(buffer);
    // broke = false;
  }

  @Override
  public synchronized void stop() {
    this.channelContext.release();
    this.futureContext.clear();
  }

  @Override
  public boolean isActive() {
    return channelContext.channel().isOpen();
  }

  @Override
  public String getId() {
    return type + ":" + id;
  }

  @Override
  public <T> T sendAndWait(Object bean) throws TransportException {
    return this.sendAndWait(bean, sendTimeout, TimeUnit.MILLISECONDS);
  }

  @Override
  public synchronized void resetChannel(AsynchronousSocketChannel channel) {
    this.channelContext.channel(channel);
    initConnectedInfo();
    start();
    logger.warn("session={} reconnected,resume session", this);
    LockSupport.unpark(thread);
  }

  @Override
  public String toString() {
    return MessageUtil.formatMessage("[{0}<-->{1},id={2}]", connectedInfo.getLocalAddress(),
        connectedInfo.getRemoteAddress(), getId());
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public void setType(Type type) {
    this.type = type;
  }

  @Override
  public ConnectedInfo getConnectedInfo() {
    return connectedInfo;
  }

  @Override
  public void setSendTimeout(int sendTimeout) {
    this.sendTimeout = sendTimeout;
  }

  @Override
  public void setReceiver(Receiver<E> receiver) {
    this.receiver = receiver;
  }

}
