/**
 * 
 */
package com.sm.common.xaio.codec;

import java.io.IOException;
import java.util.List;

import com.sm.common.libs.codec.DecodeException;
import com.sm.common.libs.codec.EncodeException;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.codec.MessageHeader;
import com.sm.common.libs.core.LoggerSupport;

/**
 * MessagePackCodec
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月23日 上午1:49:18
 */
public class MessagePackCodec extends LoggerSupport implements MessageCodec {

  private ObjectMessagePack messagePack = new ObjectMessagePack();

  @Override
  public byte[] encode(Object object) throws EncodeException {
    try {
      return messagePack.write(object);
    } catch (IOException e) {
      throw new EncodeException(e);
    }
  }

  @Override
  public <T> T decode(byte[] bytes, Class<T> clazz) throws DecodeException {
    try {
      return messagePack.read(bytes, clazz);
    } catch (IOException e) {
      throw new DecodeException(e);
    }
  }

  @Override
  public MessageHeader getHeader() {
    return new DefaultHeader();
  }
  
  public void setPackages(List<String> packages) {
    messagePack.registerPackages(packages);
  }
  
  public void setPackage(String pkg) {
    messagePack.registerPackage(pkg);
  }

}
