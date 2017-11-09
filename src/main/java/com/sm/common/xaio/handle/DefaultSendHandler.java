/**
 * 
 */
package com.sm.common.xaio.handle;

import java.nio.ByteBuffer;

import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.codec.EncodeException;
import com.sm.common.libs.codec.MessageHeader;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.SignalObject;

/**
 * 默认的发送处理器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月17日 下午9:49:09
 */
public class DefaultSendHandler extends SendHandlerSupport<Message<Integer, Object>> {

  public DefaultSendHandler(ChannelContext<Message<Integer, Object>> context,
      BusRegistry<SignalObject<Message<Integer, Object>>> busRegistry) {
    super(context, busRegistry);
  }

  @Override
  protected ByteBuffer encode(Message<Integer, Object> message, MessageHeader header) throws EncodeException {
    Object bean = message.getBean();
    byte[] bytes = context.codec().encode(bean);
    // class type length + class name
    byte[] classNameBytes = bean.getClass().getName().getBytes();
    int bodySize = 4 + classNameBytes.length + bytes.length;
    header.setBodySize(bodySize);
    header.setId(message.getId());
    header.setType(message.getType());
    byte[] headBytes = header.toArray();

    ByteBuffer byteBuffer = ByteBuffer.allocate(header.getHeaderSize() + header.getBodySize());
    // ByteBuffer byteBuffer = ByteBuffer.allocate(9 + bytes.length);
    // length + id + type (notify)
    // byteBuffer.putInt(bytes.length).putInt(id).put((byte) type);
    byteBuffer.put(headBytes);
    byteBuffer.putInt(classNameBytes.length).put(classNameBytes);
    byteBuffer.put(bytes).flip();
    return byteBuffer;
  }

  @Override
  protected Object messageKey(Message<Integer, Object> message) {
    return message.getId();
  }

}
