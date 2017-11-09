/**
 * 
 */
package com.sm.common.xaio.context;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.util.CastUtil;
import com.sm.common.libs.util.IOUtil;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.SessionManager;

/**
 * 传输通道上下文支持类
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月27日 下午1:47:53
 */
public class ChannelContextSupport<E> extends LoggerSupport implements ChannelContext<E> {

  /**
   * 异步传输通道
   */
  protected AsynchronousSocketChannel channel;

  /**
   * 设置参数用
   */
  protected Map<Object, Object> map = new ConcurrentHashMap<>();

  /**
   * 存放消息用
   */
  protected BlockingQueue<E> pendings ;

  /**
   * 编解码器
   */
  protected MessageCodec codec;

  /**
   * 绑定的会话
   */
  protected Session<E> session;

  // FIXME
  protected SessionManager<E> sessionManager;

  @Override
  public <T> ChannelContext<E> set(Object key, T value) {
    map.put(key, value);
    return this;
  }

  @Override
  public ChannelContext<E> remove(Object key) {
    map.remove(key);
    return this;
  }

  @Override
  public <T> T get(Object key) {
    Object value = map.get(key);
    return CastUtil.cast(value);
  }

  @Override
  public AsynchronousSocketChannel channel() {
    return channel;
  }

  @Override
  public ChannelContext<E> channel(AsynchronousSocketChannel channel) {
    this.channel = channel;
    return this;
  }

  @Override
  public void release() {
    map.clear();
    pendings.clear();
    IOUtil.close(channel);
  }

  @Override
  public void removeSession() {
    // FIXME
    sessionManager.removeSession(session);
  }

  @Override
  public void closeChannel() {
    IOUtil.close(channel);
  }

  @Override
  public boolean push(E message) {
    return pendings.offer(message);
  }

  @Override
  public void put(E message) {
    try {
      pendings.put(message);
    } catch (InterruptedException e) {
      logger.error("put InterruptedException", e);
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public E poll() {
    return pendings.poll();
  }

  @Override
  public E poll(long timeout, TimeUnit unit) {
    try {
      return pendings.poll(timeout, unit);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    }
  }

  @Override
  public boolean hasMessage() {
    return pendings.peek() != null;
  }

  @Override
  public MessageCodec codec() {
    return codec;
  }

  @Override
  public ChannelContext<E> codec(MessageCodec codec) {
    this.codec = codec;
    return this;
  }

  @Override
  public Session<E> session() {
    return session;
  }

  @Override
  public ChannelContext<E> session(Session<E> session) {
    this.session = session;
    return this;
  }

  @Override
  public ChannelContext<E> sessionManager(SessionManager<E> sessionManager) {
    this.sessionManager = sessionManager;
    return this;
  }
  
  @Override
  public ChannelContext<E> setPendings(BlockingQueue<E> pendings) {
    this.pendings = pendings;
    return this;
  }

}
