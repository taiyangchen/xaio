/**
 * 
 */
package com.sm.common.xaio.cluster;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.codec.MessagePackCodec;
import com.sm.common.xaio.messagepack.UserInfo;
import com.sm.common.xaio.receiver.EchoReceiver;
import com.sm.common.xaio.server.DefaultAsyncServer;

/**
 * RouterTest
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 下午2:17:32
 */
public class RouterTest extends LoggerSupport {

  private DefaultAsyncServer server1;
  private DefaultAsyncServer server2;

  private DefaultClientRouter router;

  @Before
  public void setup() {
    MessageCodec codec = new MessagePackCodec();
    Receiver<Message<Integer, Object>> receiver = new EchoReceiver<>();
    server1 = createServer("127.0.0.1", 7777);
    server1.setCodec(codec);
    server1.setReceiver(receiver);
    try {
      server1.start();
    } catch (Exception e) {
      logger.error("server start error", e);
    }

    server2 = createServer("127.0.0.1", 8888);
    server2.setCodec(codec);
    server2.setReceiver(receiver);
    try {
      server2.start();
    } catch (Exception e) {
      logger.error("server start error", e);
    }
    router = new DefaultClientRouter();
    router.setCodec(codec);
    router.setReceiver(receiver);
    router.setHosts("127.0.0.1:7777,127.0.0.1:8888");
    
    try {
      router.start();

    } catch (Exception e) {
      logger.error("client start error", e);
    }
  }

  private DefaultAsyncServer createServer(String ip, int port) {
    DefaultAsyncServer server = new DefaultAsyncServer();
    server.setHostName(ip);
    server.setPort(port);
    return server;
  }

  @Test
  public void test() {
    try {
      // Session<Message<Integer, Object>> session = client.getSession();
      for (int i = 0; i < 50000; i++) {
        UserInfo user = new UserInfo();
        user.buildUserID(i + 100).buildUserName("test" + i);
        // logger.info("batch message : " + i);
        Object value = router.sendAndWait(user);
        logger.info("the value is : [{}]", value);
      }
      // Thread.sleep(500000);
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  @After
  public void shutdown() {
    try {
      server1.stop();
      server2.stop();
      router.stop();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

}
