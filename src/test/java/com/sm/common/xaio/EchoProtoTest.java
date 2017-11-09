/**
 * 
 */
package com.sm.common.xaio;

import org.junit.Before;
import org.junit.Test;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.client.RpcProtoAsyncClient;
import com.sm.common.xaio.codec.ProtoCodec;
import com.sm.common.xaio.proto.Foo;
import com.sm.common.xaio.proto.Foo.Person;
import com.sm.common.xaio.proto.Foo.Person.Gender;
import com.sm.common.xaio.proto.RpcData.RpcProto;
import com.sm.common.xaio.receiver.EchoReceiver;
import com.sm.common.xaio.server.RpcProtoAsyncServer;

/**
 * AsyncTest
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月15日 下午12:16:03
 */
public class EchoProtoTest extends LoggerSupport {

  private RpcProtoAsyncServer server;

  private RpcProtoAsyncClient client;

  @Before
  public void setup() {
    server = new RpcProtoAsyncServer();
    server.setHostName("127.0.0.1");
    server.setPort(6666);
    MessageCodec codec = new ProtoCodec(Foo.Person.getDefaultInstance());
    Receiver<RpcProto> receiver = new EchoReceiver<>();
    server.setCodec(codec);
    server.setReceiver(receiver);
    try {
      server.start();
    } catch (Exception e) {
      logger.error("server start error", e);
    }

    client = new RpcProtoAsyncClient();
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

  @Test
  public void test() {
    try {
      long start = System.currentTimeMillis();
      Session<RpcProto> session = client.getSession();
      Person.Builder personBuilder = Foo.Person.newBuilder();
      // Person person =
      // personBuilder.setGender(Gender.FEMALE).setId(100).setName("灰机哥").setMotto("打灰机").build();

      // session.send(person);
      for (int i = 0; i < 50000; i++) {
        Person person = personBuilder.setGender(Gender.FEMALE).setId(i).setName("灰机哥").setMotto("打灰机").build();
        // Object value = session.sendAndWait(person);
        session.send(person);
        // logger.info("value is : {}", value);
      }
      long end = System.currentTimeMillis();
      logger.info("spend {}ms", end - start);
      Thread.sleep(500000);
      server.stop();
    } catch (Exception e) {
      logger.error("", e);
    }

  }

}
