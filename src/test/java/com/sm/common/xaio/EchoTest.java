/**
 * 
 */
package com.sm.common.xaio;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.util.JmxUtil;
import com.sm.common.xaio.client.DefaultAsyncClient;
import com.sm.common.xaio.codec.JsonCodec;
import com.sm.common.xaio.receiver.EchoReceiver;
import com.sm.common.xaio.server.DefaultAsyncServer;

/**
 * AsyncTest
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月15日 下午12:16:03
 */
public class EchoTest extends LoggerSupport {

  private DefaultAsyncServer server;

  private DefaultAsyncClient client;

  @Before
  public void setup() {
    server = new DefaultAsyncServer();
    server.setHostName("127.0.0.1");
    server.setPort(6666);
    MessageCodec codec = new JsonCodec();
    Receiver<Message<Integer, Object>> receiver = new EchoReceiver<>();
    server.setCodec(codec);
    server.setReceiver(receiver);
    try {
      server.start();
    } catch (Exception e) {
      logger.error("server start error", e);
    }

    client = new DefaultAsyncClient();
    client.setAutoCreateSession(true);
    client.setHostName("127.0.0.1");
    client.setPort(6666);
    client.setCodec(codec);
    client.setReceiver(receiver);
    try {
      client.start();

    } catch (Exception e) {
      logger.error("client start error", e);
    }
  }

  // private Receiver<Object> initReceiver() {
  // ReceiverHub receiver = new ReceiverHub();
  // AnyReceiverSelector selector = new AnyReceiverSelector();
  //
  // receiver.setSelector(selector);
  // selector.setReceiver(new EchoReceiver());
  // return CastUtil.cast(receiver);
  // }

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
      Object value = client.getSession().sendAndWait(new String("hello world!"), 10, TimeUnit.MINUTES);
      logger.info("the value is : [{}]", value);
      value = session.sendAndWait(new String("AsyncChannelTest"));
      logger.info("the value is : [{}]", value);
      for (int i = 0; i < 50000; i++) {
        // logger.info("batch message : " + i);
        value = session.sendAndWait("message : " + i);
        logger.info("the value is : [{}]", value);
      }

    } catch (Exception e) {
      logger.error("", e);
    }
  }

  @Test
  public void testCallback() {
    logger.info("pid=[{}]", JmxUtil.getPid());
    ResponseCallback<String> callback = new ResponseCallback<String>() {
      @Override
      public void onException(Throwable e) {
        logger.error("callback exception:", e);
      }

      @Override
      public void onSuccess(String result) {
        logger.info("callback success,result:[{}]", result);
      }
    };

    try {
      Session<Message<Integer, Object>> session = client.getSession();
      // session.send(callback, "hello world!");
      // session.send(callback, "AsyncChannelTest");
      for (int i = 0; i < 50000; i++) {
        session.send(callback, "message : " + i);
      }
      Thread.sleep(500000);
      server.stop();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  @Test
  public void test() {
    try {
//      Session<Message<Integer, Object>> session = client.getSession();
      Object value = client.getSession().sendAndWait(new String("hello world!"));
      logger.info("the value is : [{}]", value);
      value = client.getSession().sendAndWait(new String("AsyncChannelTest"));
      logger.info("the value is : [{}]", value);
      for (int i = 0; i < 50000; i++) {
        // logger.info("batch message : " + i);
        try {
          value = client.sendAndWait(("message : " + i));
          logger.info("the value is : [{}]", value);
        } catch (Exception e) {
          logger.error("", e);
        }

      }
      Thread.sleep(500000);
      server.stop();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  @Test
  public void testBig() {
    try {
      Session<Message<Integer, Object>> session = client.getSession();

      for (int i = 0; i < 20; i++) {
        String message = createBig();
        Message<Integer, String> value = session.sendAndWait(message);
        logger.info("the value length is : [{}]", value.getBean().getBytes().length);
      }
      Thread.sleep(500000);
      server.stop();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

  private String createBig() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      builder.append("message : " + i);
    }

    return builder.toString();
  }

}
