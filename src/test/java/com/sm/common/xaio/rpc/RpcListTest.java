/**
 * 
 */
package com.sm.common.xaio.rpc;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.util.CastUtil;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.client.DefaultAsyncClient;
import com.sm.common.xaio.codec.MessagePackCodec;
import com.sm.common.xaio.messagepack.UserInfo;
import com.sm.common.xaio.receiver.RpcStyleDispatcher;
import com.sm.common.xaio.server.DefaultAsyncServer;

/**
 * RpcTest
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月15日 下午12:16:03
 */
public class RpcListTest extends LoggerSupport {

  private DefaultAsyncServer server;

  private DefaultAsyncClient client;

  private HelloworldList helloworldApi;

  @Before
  public void setup() {
    server = new DefaultAsyncServer();
    server.setHostName("127.0.0.1");
    server.setPort(6666);
    MessageCodec codec = new MessagePackCodec();
    RpcStyleDispatcher dispatcher = new RpcStyleDispatcher();
    Receiver<Message<Integer, Object>> receiver = CastUtil.cast(dispatcher);
    dispatcher.setCourse(new HelloworldImpl());
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
//    client.setReceiver(receiver);
    try {
      client.start();

    } catch (Exception e) {
      logger.error("client start error", e);
    }

    try {
      initApi(client);
    } catch (Exception e) {
      logger.error("initApi error", e);
    }

  }

  private void initApi(DefaultAsyncClient client) throws Exception {
    InvokerAllProxyCreator rpc = new InvokerAllProxyCreator();
    rpc.setPeer(client);
    rpc.setServiceInterfaces(new Class<?>[] {HelloworldList.class});

    helloworldApi = RpcProxyFactory.createProxy(rpc);
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

    Thread.sleep(10000);
    server.stop();
  }

  private void doTest() {
    try {
      Object name = helloworldApi.getName("test");
      logger.info("name is : [{}]", name);
      helloworldApi.printMessage("luck!!!", 10);
      for (int i = 0; i < 50000; i++) {
        // logger.info("batch message : " + i);

      }

    } catch (Exception e) {
      logger.error("", e);
    }
  }
  
  @Test
  public void testUserInfo() {
    for(int i=0;i<100;i++) {
      UserInfo user = new UserInfo();
      user.buildUserID(i+100).buildUserName("test" + i);
      List<UserInfo> result = helloworldApi.echo(user);
      logger.info("user:{}",result);
    }
  }

  @Test
  public void test() {
//    ResponseCallback<String> callback = new ResponseCallback<String>() {
//      @Override
//      public void onException(Throwable e) {
//        logger.error("callback exception:", e);
//      }
//
//      @Override
//      public void onSuccess(String result) {
//        logger.info("callback success,result:[{}]", result);
//      }
//    };
    try {
      Object name = helloworldApi.getName("test");
      List<String> list = CastUtil.cast(name);
      logger.info("name is : {}", list);
      helloworldApi.printMessage("luck!!!", 10);
//      for(int i=0;i<10;i++) {
//        try {
//          helloworldApi.timeout();
//        }catch(Exception e) {
//          logger.info("timeout error", e);
//        }
//        
//      }
      for (int i = 0; i < 50000; i++) {
        Object value = helloworldApi.getName("test : " + i);
        logger.info("value is {}", value);
      }
      helloworldApi.printMessage("testtest");
      helloworldApi.printMessage("xxx", 5);
//      helloworldApi.messageCallback(callback, "callback");

      Thread.sleep(10000);
      server.stop();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

}
