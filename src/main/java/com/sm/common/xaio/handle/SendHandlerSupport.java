/**
 * 
 */
package com.sm.common.xaio.handle;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sm.common.libs.able.Sender;
import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.codec.EncodeException;
import com.sm.common.libs.codec.MessageHeader;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.SignalObject;
import com.sm.common.xaio.SignalType;

/**
 * 发送处理器支持类
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月27日 下午1:58:28
 */
public abstract class SendHandlerSupport<E> extends LoggerSupport implements CompletionHandler<Integer, E>, Sender<E> {

  /**
   * 传输通道上下文 @see ChannelContext
   */
  protected ChannelContext<E> context;

  /**
   * 写入状态控制，为防止<code>WritePendingException</code><br>
   * FIXME 此处可替换成 Composite Abortable Lock @see CompositeFastPathLock
   */
  protected AtomicBoolean writing = new AtomicBoolean(false);

  protected BusRegistry<SignalObject<E>> busRegistry;

  protected SendHandlerSupport(ChannelContext<E> context, BusRegistry<SignalObject<E>> busRegistry) {
    this.context = context;
    this.busRegistry = busRegistry;
  }

  @Override
  public void sendMessage(E message) {
    if (!context.channel().isOpen()) {
      logger.warn("channel=[{}] is closed", context.session().getConnectedInfo());
      busRegistry.getSignalManager().signal(new SignalObject<>(new ClosedChannelException(), message, SignalType.SEND));
      return;
    }
    // FIXME check?
    ByteBuffer buffer = createBuffer(message);
    if (buffer == null) {
      return;
    }
    context.set(messageKey(message), buffer);
    context.put(message);
    // 需要发送数据 FIXME
    if (!writing.get() && context.hasMessage() && writing.compareAndSet(false, true)) {
      writeData();
    }
  }

  protected void writeData() {
    E data = context.poll();
    if (data == null) {
      writing.compareAndSet(true, false);
      return;
    }
    ByteBuffer dataBuffer = context.get(messageKey(data));
    if (dataBuffer == null) {
      logger.error("create buffer error");
      writing.compareAndSet(true, false);
      return;
    }

    context.channel().write(dataBuffer, data, this);
    // context.channel().write(dataBuffer, data, this);
  }

  protected abstract Object messageKey(E message);

  private ByteBuffer createBuffer(E message) {
    MessageHeader header = context.codec().getHeader();
    try {
      final ByteBuffer byteBuffer = encode(message, header);
      return byteBuffer;
    } catch (EncodeException e) {
      logger.error("EncodeException", e);
      return null;
    }
  }

  /**
   * 对消息进行编码
   * 
   * @param message 消息体
   * @param header 消息头 @see MessageHeader
   * @return 缓冲区
   * @throws EncodeException
   */
  protected abstract ByteBuffer encode(E message, MessageHeader header) throws EncodeException;

  protected void setWriteIdle() {
    writing.compareAndSet(true, false);
  }

  @Override
  public void completed(Integer result, E message) {
    Object key = messageKey(message);
    ByteBuffer buffer = context.get(key);
    // FIXME 多线程竞争 buffer 可能为 null
    if (buffer == null) {
      logger.warn("message maybe handled with other thread,which key=[{}]", key);
      setWriteIdle();
      return;
    }
    if (buffer.hasRemaining()) {
      context.channel().write(buffer, message, this);
      return;
    }

    if (context.hasMessage()) {
      E data = context.poll();
      ByteBuffer byteBuffer = context.get(messageKey(data));
      if (byteBuffer == null) {
        logger.error("create buffer error");
        setWriteIdle();
        return;
      }

      context.channel().write(byteBuffer, data, this);
      return;
    }
    // FIXME
    context.remove(key);
    setWriteIdle();
  }

  @Override
  public void failed(Throwable exc, E message) {
    // context.push(message);
    context.remove(messageKey(message));
    logger.warn("send failed,exc={}", exc.getMessage());
    // sync
    busRegistry.getSignalManager().signal(new SignalObject<>(exc, message, SignalType.SEND));
    setWriteIdle();
  }

}
