/**
 * 
 */
package com.sm.common.xaio;

import org.junit.Before;
import org.junit.Test;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.client.DefaultAsyncClient;
import com.sm.common.xaio.codec.JsonCodec;
import com.sm.common.xaio.receiver.DebugReceiver;
import com.sm.common.xaio.server.DefaultAsyncServer;

/**
 * DebugTest
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月15日 下午12:16:03
 */
public class DebugTest extends LoggerSupport {

  private DefaultAsyncServer server;

  private DefaultAsyncClient client;

  @Before
  public void setup() {
    server = new DefaultAsyncServer();
    server.setHostName("127.0.0.1");
    server.setPort(6666);
    MessageCodec codec = new JsonCodec();
    Receiver<Message<Integer, Object>> receiver = new DebugReceiver<>();
    server.setCodec(codec);
    server.setReceiver(receiver);
    try {
      server.start();
    } catch (Exception e) {
      logger.error("server start error", e);
    }

    client = new DefaultAsyncClient();
    client.setHostName("127.0.0.1");
    client.setPort(6666);

    client.setCodec(codec);
    client.setReceiver(receiver);
    client.setAutoCreateSession(true);
    try {
      client.start();

    } catch (Exception e) {
      logger.error("client start error", e);
    }
  }

  @Test
  public void test2() throws Exception {
    new Thread() {
      @Override
      public void run() {
        doTest();
      }
    }.start();

    new Thread() {
      @Override
      public void run() {
        doTest();
      }
    }.start();

    Thread.sleep(500000);
    server.stop();
  }

  private void doTest() {
    try {
      Session<Message<Integer, Object>> session = client.getSession();
      session.send(new String("hello world!"));

      session.send(new String("AsyncChannelTest"));
      for (int i = 0; i < 100; i++) {
        // logger.info("batch message : " + i);
        session.send("message : " + i);
      }

    } catch (Exception e) {
      logger.error("", e);
    }
  }

  @Test
  public void test() {
    try {
      Session<Message<Integer, Object>> session = client.getSession();
      session.send(new String("hello world!"));

      session.send(new String("AsyncChannelTest"));
      for (int i = 0; i < 50000; i++) {
        // logger.info("batch message : " + i);
        session.send("message : " + i);
      }
      Thread.sleep(500000);
      server.stop();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

}
