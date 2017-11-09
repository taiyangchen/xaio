/**
 * 
 */
package com.sm.common.xaio.session;

import java.io.IOException;
import java.nio.ByteBuffer;
// import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.MessageLite;
import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.libs.core.ResponseFuture;
import com.sm.common.libs.util.CastUtil;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.SignalObject;
import com.sm.common.xaio.SignalType;
import com.sm.common.xaio.TransportException;
import com.sm.common.xaio.handle.RpcProtoReceiveHandler;
import com.sm.common.xaio.handle.RpcProtoSendHandler;
import com.sm.common.xaio.proto.RpcData.RpcProto;

/**
 * Proto RPC的会话
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月23日 下午4:09:35
 */
public class RpcProtoSession extends SessionTemplate<RpcProto> {

  public RpcProtoSession(ChannelContext<RpcProto> channelContext) {
    super(channelContext);
  }

  @Override
  protected void initSendHandler() {
    sender = new RpcProtoSendHandler(channelContext, busRegistry);
  }

  @Override
  protected void initReceiveHandler(ByteBuffer buffer) {
    channelContext.channel().read(buffer, buffer, new RpcProtoReceiveHandler(channelContext, busRegistry));
  }

  @Override
  public void send(Object bean) {
    // 若为RpcProto
    if (bean instanceof RpcProto) {
      RpcProto message = CastUtil.cast(bean);
      sender.sendMessage(message);
      return;
    }
    // 转化为RpcProto
    MessageLite message = CastUtil.cast(bean);
    RpcProto rpcData = RpcProtoCreator.create(message, type);
    sender.sendMessage(rpcData);
  }

  @Override
  public <T> T sendAndWait(Object bean, long duration, TimeUnit unit) throws TransportException {
    // 设置异步响应对象
    ResponseFuture<Object> future = null;
    try {
      if (bean instanceof RpcProto) {
        RpcProto message = CastUtil.cast(bean);
        future = addFutureToPending(message);
      } else {
        MessageLite message = CastUtil.cast(bean);
        RpcProto rpcData = RpcProtoCreator.create(message, type);
        future = addFutureToPending(rpcData);
      }
      T result = CastUtil.cast(future.get(duration, unit));
      return result;
    } catch (Exception e) {
      throw new TransportException(e);
    } finally {
      if (future != null) {
        future.cancel(false);
      }
    }
  }

  /**
   * 添加到异步响应映射表
   * 
   * @param message 消息对象 @see RpcProto
   * @return 异步响应对象 @see ResponseFuture
   */
  private ResponseFuture<Object> addFutureToPending(RpcProto message) {
    // Object bean = message.getBean();
    ResponseFuture<Object> future = new ResponseFuture<>();
    futureContext.put(message.getRequestid(), future);
    // classContext.put(id, bean.getClass());
    sender.sendMessage(message);

    return future;
  }

  @Override
  public void messageReceived(RpcProto message) {
    // 从异步响应映射表移除匹配的对象
    Object object = futureContext.remove(message.getRequestid());
    // 同步响应
    if (object instanceof ResponseFuture) {
      ResponseFuture<Object> future = CastUtil.cast(object);
      future.set(message);
    }
    // 异步响应
    if (object instanceof ResponseCallback) {
      ResponseCallback<Object> callback = CastUtil.cast(object);
      try {
        // FIXME message or body ?
        callback.onSuccess(message);
      } catch (Exception e) {
        callback.onException(e);
      }
    }
    // 消息接收器额外处理
    if (receiver != null) {
      receiver.messageReceived(message);
    }

  }

  @Override
  public <T> void send(ResponseCallback<T> callback, Object bean) {
    RpcProto message = CastUtil.cast(bean);

    futureContext.put(message.getRequestid(), callback);
    // request 0
    sender.sendMessage(message);
  }

  @Override
  public void signalFired(SignalObject<RpcProto> signal) {
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

  private void fireSend(SignalObject<RpcProto> signal) {
    RpcProto message = signal.getMessage();
    Object value = futureContext.get(message.getRequestid());
    if (value instanceof ResponseCallback) {
      ResponseCallback<Object> callback = CastUtil.cast(value);
      callback.onException(signal.getExc());
      // channel broken
      if (signal.getExc() instanceof IOException) {
        logger.warn("session={} broken,channel closed", this);
        // LockSupport.park();
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
