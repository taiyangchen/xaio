/**
 * 
 */
package com.sm.common.xaio;

import java.util.concurrent.locks.LockSupport;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.able.ResponseCallback;
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
public class ClientStart2 {

  protected static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClientStart2.class);

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

    // Semaphore block = new Semaphore(1);O
    try {
      client.start();
      final Session<Message<Integer, Object>> session = client.getSession();
      // session.send("test");
      for (int i = 0; i < 10000; i++) {
//        Thread.sleep(5000);
        try {
          final String value = "test : " + i;
          session.send(new ResponseCallback<Object>() {
            @Override
            public void onException(Throwable e) {
              if (e instanceof java.nio.channels.ClosedChannelException) {
                LockSupport.park();
                logger.info("xxxxxxxxxxxxxxxxxxxxx");
                // block.compareAndSet(false, true);
                // LockSupport.park(blocker);
                session.send(new ResponseCallback<Object>() {
                  @Override
                  public void onException(Throwable e) {
                    logger.error("onException error", e);
                  }

                  @Override
                  public void onSuccess(Object result) {
                    logger.info("value=[{}]", result);

                  }
                }, value);
              }
            }
            @Override
            public void onSuccess(Object result) {
              logger.info("value=[{}]", result);
            }
          }, value);
//          Thread.sleep(10);
          // Object value = session.sendAndWait("test : " + i);
          // logger.info("value=[{}]", value);
//          logger.info("active=[{}]", client.isActive());
        } catch (Exception e) {
          logger.error("", e);
        }

      }
      Thread.sleep(100000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
