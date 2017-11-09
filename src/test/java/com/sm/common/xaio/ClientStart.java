/**
 * 
 */
package com.sm.common.xaio;

import java.util.concurrent.locks.LockSupport;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.xaio.client.DefaultAsyncClient;
import com.sm.common.xaio.codec.JsonCodec;
import com.sm.common.xaio.receiver.EchoReceiver;

/**
 * ClientStart
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月27日 下午7:53:28
 */
public class ClientStart {

  protected static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClientStart.class);

  public static void main(String[] args) {
    DefaultAsyncClient client = new DefaultAsyncClient();
    client.setHostName("127.0.0.1");
    client.setPort(6666);
    MessageCodec codec = new JsonCodec();
    Receiver<Message<Integer, Object>> receiver = new EchoReceiver<>();
    // MessageCodec codec = new StringCodec();
    // Receiver<Message<Integer, Object>> receiver = new DebugReceiver<>();
    client.setCodec(codec);
    client.setReceiver(receiver);
    try {
      client.start();
      Session<Message<Integer, Object>> session = client.getSession();
      // session.send("test");
      for (int i = 0; i < 100000; i++) {
//        Thread.sleep(5000);
        try {
          System.out.println("send test : " + i);
//          session.send(new ResponseCallback<Object>() {
//            @Override
//            public void onException(Throwable e) {
//              logger.error("onException error", e);
//            }
//
//            @Override
//            public void onSuccess(Object result) {
//              logger.info("value=[{}]", result);
//            }
//          }, "test : " + i);
          Object value = session.sendAndWait("test : " + i);
          if(value instanceof java.nio.channels.ClosedChannelException) {
            LockSupport.park();
            value = session.sendAndWait("test : " + i);
          }
          logger.info("value=[{}]", value);
//          logger.info("active=[{}]", client.isActive());
        } catch (Exception e) {
          logger.error("", e);
//          Object value = session.sendAndWait("test : " + i);
//          logger.info("value=[{}]", value);
//          logger.info("active=[{}]", client.isActive());
        }

      }
      Thread.sleep(100000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
