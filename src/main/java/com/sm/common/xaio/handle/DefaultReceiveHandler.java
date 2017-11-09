/**
 * 
 */
package com.sm.common.xaio.handle;

import java.nio.ByteBuffer;

import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.codec.MessageHeader;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.SignalObject;
import com.sm.common.xaio.util.SessionUtil;

/**
 * 默认的接收处理器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月17日 下午9:49:23
 */
public class DefaultReceiveHandler extends ReceiveHandlerSupport<Message<Integer, Object>> {

  public DefaultReceiveHandler(ChannelContext<Message<Integer, Object>> context,
      BusRegistry<SignalObject<Message<Integer, Object>>> busRegistry) {
    super(context, busRegistry);
  }

  @Override
  protected void handleBody(MessageHeader header, ByteBuffer body) throws Exception {
    body.flip();
    int classTypeLen = body.getInt();
    byte[] classBytes = new byte[classTypeLen];
    body.get(classBytes);
    Class<?> clazz = Class.forName(new String(classBytes));
    byte[] bodyBytes = new byte[body.remaining()];
    body.get(bodyBytes);
    Object value = context.codec().decode(bodyBytes, clazz);
    Message<Integer, Object> message = new Message<>(header.getType(), value, header.getId());
    Session<Message<Integer, Object>> session = context.session();
    // session = SessionUtil.bind(message, session);
    // FIXME receiver need to unbind
    session.messageReceived(SessionUtil.bind(message, session));
    // session.messageReceived(message);
  }

}
