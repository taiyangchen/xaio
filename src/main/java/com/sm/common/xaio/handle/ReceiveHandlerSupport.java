/**
 * 
 */
package com.sm.common.xaio.handle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.codec.MessageHeader;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.SignalObject;
import com.sm.common.xaio.SignalType;


/**
 * 接收处理器支持类
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月17日 下午9:49:23
 */
public abstract class ReceiveHandlerSupport<E> extends LoggerSupport implements CompletionHandler<Integer, ByteBuffer> {

  /**
   * 传输通道上下文 @see ChannelContext
   */
  protected ChannelContext<E> context;

  protected BusRegistry<SignalObject<E>> busRegistry;

  protected ReceiveHandlerSupport(ChannelContext<E> context, BusRegistry<SignalObject<E>> busRegistry) {
    this.context = context;
    this.busRegistry = busRegistry;
  }

  @Override
  public void completed(Integer result, ByteBuffer attachment) {
    if (result == -1) {
      try {
        logger.warn("read failed,close session={}", context.channel().getRemoteAddress());
      } catch (IOException e) {
        logger.error(e.getMessage());
      }

      release();
      return;
    }

    if (result == 0 || attachment.hasRemaining()) {
      context.channel().read(attachment, attachment, this);
      return;
    }

    try {
      handlerData(attachment);
    } catch (Exception e) {
      logger.error("handlerHeader error", e);
    }

    attachment.clear();
    context.channel().read(attachment, attachment, this);
  }

  private void handlerData(ByteBuffer attachment) throws Exception {
    final MessageHeader header = context.codec().getHeader();
    header.parseForm(attachment.array());
    ByteBuffer body = readBody(header);

    handleBody(header, body);
  }

  private ByteBuffer readBody(MessageHeader header) {
    int bodySize = header.getBodySize();
    ByteBuffer body = ByteBuffer.allocate(bodySize);
    // FIXME sync
    while (body.hasRemaining()) {
      Integer result = read(body, bodySize);
      if (result == -1) {
        logger.error("read failed,close session");
        context.release();
        return null;
      }
    }

    return body;
  }

  // FIXME
  private Integer read(ByteBuffer body, int bodySize) {
    try {
      Future<Integer> future = context.channel().read(body);
      return future.get();
    } catch (Exception e) {
      // may be java.util.concurrent.ExecutionException: java.io.IOException:
      // java.lang.OutOfMemoryError: Direct buffer memory
      logger.error("body size = [{}]", bodySize, e);
      return -1;
    }
  }

  /**
   * 读取消息体
   * 
   * @param header 消息头 @see MessageHeader
   * @param body 消息体
   * @throws Exception
   */
  protected abstract void handleBody(MessageHeader header, ByteBuffer body) throws Exception;

  @Override
  public void failed(Throwable exc, ByteBuffer attachment) {
    logger.warn("read failed,close session");
    busRegistry.getSignalManager().signal(new SignalObject<>(exc, (E) null, SignalType.RECEIVE));

    release();
  }

  protected void release() {
    context.removeSession();
  }

}
