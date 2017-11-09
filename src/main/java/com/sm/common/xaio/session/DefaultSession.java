/**
 * 
 */
package com.sm.common.xaio.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.libs.core.ResponseFuture;
import com.sm.common.libs.util.CastUtil;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.SignalObject;
import com.sm.common.xaio.SignalType;
import com.sm.common.xaio.TransportException;
import com.sm.common.xaio.handle.DefaultReceiveHandler;
import com.sm.common.xaio.handle.DefaultSendHandler;

/**
 * 默认的会话实现
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月9日 下午6:12:31
 */
public class DefaultSession extends SessionTemplate<Message<Integer, Object>> {

  public DefaultSession(ChannelContext<Message<Integer, Object>> channelContext) {
    super(channelContext);
  }

  @Override
  protected void initSendHandler() {
    sender = new DefaultSendHandler(channelContext, busRegistry);
  }

  @Override
  protected void initReceiveHandler(ByteBuffer buffer) {
    // FIXME
    channelContext.channel().read(buffer, buffer, new DefaultReceiveHandler(channelContext, busRegistry));
  }

  @Override
  public void send(Object bean) {
    if (bean instanceof Message) {
      Message<Integer, Object> message = CastUtil.cast(bean);
      sender.sendMessage(message);
      return;
    }

    int type = (this.type == Type.CLIENT) ? 2 : 1;
    sender.sendMessage(new Message<>(type, bean));
  }

  @Override
  public <T> T sendAndWait(Object bean, long duration, TimeUnit unit) throws TransportException {
    ResponseFuture<Object> future = null;
    try {
      if (bean instanceof Message) {
        Message<Integer, Object> message = CastUtil.cast(bean);
        future = addFutureToPending(message);
      } else {
        future = addFutureToPending(new Message<>(0, bean));
      }
      T result = CastUtil.cast(future.get(duration, unit));
      return result;
    } catch (Throwable e) {
      throw new TransportException(e);
    } finally {
      if (future != null) {
        future.cancel(false);
      }
    }
  }

  private ResponseFuture<Object> addFutureToPending(Message<Integer, Object> message) {
    // Object bean = message.getBean();
    ResponseFuture<Object> future = new ResponseFuture<>();
    futureContext.put(message.getId(), future);
    // classContext.put(id, bean.getClass());
    sender.sendMessage(message);

    return future;
  }

  @Override
  public void messageReceived(Message<Integer, Object> message) {
    Object object = futureContext.remove(message.getId());
    if (object instanceof ResponseFuture) {
      ResponseFuture<Object> future = CastUtil.cast(object);
      future.set(message);
    }

    if (object instanceof ResponseCallback) {
      ResponseCallback<Object> callback = CastUtil.cast(object);
      try {
        // FIXME message or bean ?
        callback.onSuccess(message.getBean());
      } catch (Exception e) {
        callback.onException(e);
      }
    }

    if (receiver != null) {
      receiver.messageReceived(message);
    }

  }

  @Override
  public <T> void send(ResponseCallback<T> callback, Object bean) {
    Message<Integer, Object> message;
    if (bean instanceof Message) {
      message = CastUtil.cast(bean);
    } else {
      message = new Message<>(0, bean);
    }

    futureContext.put(message.getId(), callback);
    // request 0
    sender.sendMessage(message);
  }

  @Override
  public void signalFired(SignalObject<Message<Integer, Object>> signal) {
    if (signal.getType() == SignalType.SEND) {
      fireSend(signal);
      return;
    }
    if (signal.getType() == SignalType.RECEIVE) {
      if (signal.getExc() instanceof IOException) {
        logger.warn("session={} broken,channel closed", this);
      }
    }
  }

  private void fireSend(SignalObject<Message<Integer, Object>> signal) {
    Message<Integer, Object> message = signal.getMessage();
    // Object value = futureContext.get(message.getId());
    Object value = futureContext.remove(message.getId());
    if (value instanceof ResponseCallback) {
      ResponseCallback<Object> callback = CastUtil.cast(value);
      callback.onException(signal.getExc());
      // channel broken
      if (signal.getExc() instanceof IOException) {
        logger.warn("session={} broken,channel closed", this);
      }
      return;
    }

    if (value instanceof ResponseFuture) {
      ResponseFuture<Object> future = CastUtil.cast(value);
      future.set(signal.getExc());
      // channel broken
      if (signal.getExc() instanceof IOException) {
        logger.warn("session={} broken,channel closed", this);
      }
    }
  }

}
