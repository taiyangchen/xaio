/**
 * 
 */
package com.sm.common.xaio.handle;

import java.nio.ByteBuffer;

import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.codec.MessageHeader;
import com.sm.common.libs.util.CastUtil;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.SignalObject;
import com.sm.common.xaio.proto.RpcData;
import com.sm.common.xaio.util.SessionUtil;

/**
 * proto RPC 的接收处理
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月17日 下午9:49:23
 */
public class RpcProtoReceiveHandler extends ReceiveHandlerSupport<RpcData.RpcProto> {

  public RpcProtoReceiveHandler(ChannelContext<RpcData.RpcProto> context,
      BusRegistry<SignalObject<RpcData.RpcProto>> busRegistry) {
    super(context, busRegistry);
  }

  @Override
  protected void handleBody(MessageHeader header, ByteBuffer body) throws Exception {
    // java class 类型
    Class<?> clazz = header.getClassType();
    byte[] bytes = body.array();
    // 解码
    Object value = context.codec().decode(bytes, clazz);
    RpcData.RpcProto rrp = CastUtil.cast(value);
    // 接收器处理
    Session<RpcData.RpcProto> session = context.session();
    // FIXME receiver need to unbind
    session.messageReceived(SessionUtil.bind(rrp, session));
    // session.messageReceived(message);
  }

}
