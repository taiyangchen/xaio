/**
 * 
 */
package com.sm.common.xaio.codec;

import java.io.IOException;

import org.junit.Test;
import org.msgpack.MessagePack;

import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.messagepack.UserInfo;

/**
 * MessagePackTest
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月9日 下午5:43:29
 */
public class MessagePackTest extends LoggerSupport {

  private MessagePack messagePack = new MessagePack();

  @Test
  public void test() throws IOException {
    UserInfo userInfo = new UserInfo();
    userInfo.buildUserID(250).buildUserName("saolangjian");

    // 序列化
    byte[] bs = messagePack.write(userInfo);
    logger.info("byte array's length is : {}", bs.length);
    // 反序列化
    UserInfo serializableUserinfo = messagePack.read(bs, UserInfo.class);
    logger.info("userinfo=[{}]", serializableUserinfo);

    String test = "test";
    bs = messagePack.write(test);
    logger.info("byte array's length is : {}", bs.length);
    String value = messagePack.read(bs, String.class);
    logger.info("value=[{}]", value);

  }

}
