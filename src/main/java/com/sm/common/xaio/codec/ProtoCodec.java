/**
 * 
 */
package com.sm.common.xaio.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.sm.common.libs.codec.DecodeException;
import com.sm.common.libs.codec.EncodeException;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.codec.MessageHeader;
import com.sm.common.libs.core.LoggerSupport;

/**
 * codec for protobuf
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月18日 下午4:22:02
 */
public class ProtoCodec extends LoggerSupport implements MessageCodec {

  private final MessageLite prototype;

  public ProtoCodec(MessageLite prototype) {
    this.prototype = prototype.getDefaultInstanceForType();
  }

  @Override
  public byte[] encode(Object object) throws EncodeException {
    if (object instanceof MessageLite) {
      return encode((MessageLite) object);
    }
    if (object instanceof MessageLite.Builder) {
      return encode((MessageLite.Builder) object);
    }
    return null;
  }

  private byte[] encode(MessageLite messageLite) throws EncodeException {
    return messageLite.toByteArray();
  }

  private byte[] encode(MessageLite.Builder messageLiteBuilder) throws EncodeException {
    return messageLiteBuilder.build().toByteArray();
  }

  @Override
  public <T> T decode(byte[] bytes, Class<T> clazz) throws DecodeException {
    try {
      @SuppressWarnings("unchecked")
      T result = (T) prototype.getParserForType().parseFrom(bytes);
      return result;
    } catch (InvalidProtocolBufferException e) {
      logger.error("InvalidProtocolBufferException error", e);
      return null;
    }
  }

  @Override
  public MessageHeader getHeader() {
    return new ProtoHeader();
  }

}
