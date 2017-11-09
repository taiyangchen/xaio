/**
 * 
 */
package com.sm.common.xaio.handle;

import java.nio.ByteBuffer;

import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.codec.EncodeException;
import com.sm.common.libs.codec.MessageHeader;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.SignalObject;
import com.sm.common.xaio.proto.RpcData;
import com.sm.common.xaio.proto.RpcData.RpcProto;

/**
 * proto RPC 的发送处理
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月17日 下午9:49:09
 */
public class RpcProtoSendHandler extends SendHandlerSupport<RpcData.RpcProto> {

  public RpcProtoSendHandler(ChannelContext<RpcData.RpcProto> context,
      BusRegistry<SignalObject<RpcData.RpcProto>> busRegistry) {
    super(context, busRegistry);
  }

  @Override
  protected ByteBuffer encode(RpcData.RpcProto message, MessageHeader header) throws EncodeException {
    // body编码
    byte[] bytes = context.codec().encode(message);
    // 设置头信息
    header.setBodySize(bytes.length);
    header.setType(message.getType());
    // 头信息编码
    byte[] headBytes = header.toArray();
    // 分配Buffer
    ByteBuffer byteBuffer = ByteBuffer.allocate(header.getHeaderSize() + header.getBodySize());
    // header编码 + body编码
    byteBuffer.put(headBytes).put(bytes).flip();
    return byteBuffer;
  }

  @Override
  protected Object messageKey(RpcProto message) {
    return message.getRequestid();
  }

}
