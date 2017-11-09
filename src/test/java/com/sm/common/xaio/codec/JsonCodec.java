/**
 * 
 */
package com.sm.common.xaio.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sm.common.libs.codec.DecodeException;
import com.sm.common.libs.codec.EncodeException;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.codec.MessageHeader;

/**
 * JsonCodec
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午8:50:39
 */
public class JsonCodec implements MessageCodec {
  /**
   * 对象匹配映射
   */
  private final ObjectMapper mapper;

  public JsonCodec() {
    mapper = new ObjectMapper();

    // ignoring unknown properties makes us more robust to changes in the
    // schema
    // mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // This will allow including type information all non-final types. This
    // allows correct
    // serialization/deserialization of generic collections, for example
    // List<MyType>.
    mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  }

  @Override
  public byte[] encode(Object object) throws EncodeException {
    try {
      return mapper.writeValueAsBytes(object);
    } catch (Exception e) {
      throw new EncodeException(e);
    }
  }

  @Override
  public <T> T decode(byte[] bytes, Class<T> clazz) throws DecodeException {
    try {
      return mapper.readValue(bytes, clazz);
    } catch (Exception e) {
      throw new DecodeException(e);
    }
  }

  @Override
  public MessageHeader getHeader() {
    return new DefaultHeader();
  }

}
