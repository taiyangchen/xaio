/**
 * 
 */
package com.sm.common.xaio;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.xaio.codec.JsonCodec;
import com.sm.common.xaio.receiver.EchoReceiver;
import com.sm.common.xaio.server.DefaultAsyncServer;

/**
 * ServerStart
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月27日 下午7:54:26
 */
public class ServerStart {

  public static void main(String[] args) {
    DefaultAsyncServer server = new DefaultAsyncServer();
    server.setHostName("127.0.0.1");
    server.setPort(6666);
    MessageCodec codec = new JsonCodec();
    Receiver<Message<Integer, Object>> receiver = new EchoReceiver<>();
    server.setCodec(codec);
    server.setReceiver(receiver);
    try {
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
