package com.sm.common.xaio.cluster;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.bus.DefaultBusRegistry;
import com.sm.common.libs.codec.MessageCodec;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.bo.PeerInfo;
import com.sm.common.xaio.bo.ServerGroup;
import com.sm.common.xaio.bo.ServerInfo;
import com.sm.common.xaio.codec.MessagePackCodec;
import com.sm.common.xaio.messagepack.UserInfo;
import com.sm.common.xaio.receiver.EchoReceiver;
import com.sm.common.xaio.register.ZkRegister;
import com.sm.common.xaio.server.DefaultAsyncServer;
import com.sm.common.xaio.subscribe.ZkSubscribe;
import com.sm.common.xaio.zk.XzkClient;

/**
 * GroupTest
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月29日 上午10:38:39
 */
public class GroupTest extends LoggerSupport {

  private DefaultAsyncServer server1;
  private DefaultAsyncServer server2;
  private DefaultAsyncServer server3;

  private DefaultClientGroup group;

  private XzkClient xzkClient;

  private ZkRegister register;

  private BusRegistry<ServerGroup> busRegistry = new DefaultBusRegistry<>();

  private ZkSubscribe subscribe;

  @Before
  public void setup() {
    startServer();
    initZks();
    registerServer();
    try {
      subscribeServers();
    } catch (Exception e) {
      logger.error("subscribeServers error", e);
    }
  }

  private void subscribeServers() throws Exception {
    subscribe = new ZkSubscribe();
    subscribe.setBusRegistry(busRegistry);
    subscribe.setXzkClient(xzkClient);
    group.setBusRegistry(busRegistry);
    group.setSubscribe(subscribe);
    group.setCategory("download@spider");
    group.start();
  }

  private void registerServer() {
    register = new ZkRegister();
    register.setXzkClient(xzkClient);
    PeerInfo peer = server1.getPeer();
    ServerInfo serverInfo = new ServerInfo();
    serverInfo.setPeerInfo(peer);
    serverInfo.setDomain("spider");
    serverInfo.setGroup("download");
    register.registerServer(serverInfo);

    peer = server2.getPeer();
    serverInfo.setPeerInfo(peer);
    register.registerServer(serverInfo);
  }
  
  private void startServerNew() {
    MessageCodec codec = new MessagePackCodec();
    Receiver<Message<Integer, Object>> receiver = new EchoReceiver<>();
    server3 = createServer(9999);
    server3.setCodec(codec);
    server3.setReceiver(receiver);
    try {
      server3.start();
    } catch (Exception e) {
      logger.error("server start error", e);
    }
    
    PeerInfo peer = server3.getPeer();
    ServerInfo serverInfo = new ServerInfo();
    serverInfo.setPeerInfo(peer);
    serverInfo.setDomain("spider");
    serverInfo.setGroup("download");
    register.registerServer(serverInfo);
  }

  private void startServer() {
    MessageCodec codec = new MessagePackCodec();
    Receiver<Message<Integer, Object>> receiver = new EchoReceiver<>();
    server1 = createServer(7777);
    server1.setCodec(codec);
    server1.setReceiver(receiver);
    try {
      server1.start();
    } catch (Exception e) {
      logger.error("server start error", e);
    }

    server2 = createServer(8888);
    server2.setCodec(codec);
    server2.setReceiver(receiver);
    try {
      server2.start();
    } catch (Exception e) {
      logger.error("server start error", e);
    }

    group = new DefaultClientGroup();
    group.setCodec(codec);
    group.setReceiver(receiver);

  }

  private void initZks() {
    xzkClient = new XzkClient();
    String servers = "10.101.174.195:21811,10.101.174.195:21812,10.101.174.195:21813";
    xzkClient.setServers(servers);
    xzkClient.start();
  }

  private DefaultAsyncServer createServer(int port) {
    DefaultAsyncServer server = new DefaultAsyncServer();
    // server.setHostName(ip);
    server.setPort(port);
    return server;
  }

  @Test
  public void test() {
    new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(2000);
          startServerNew();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        
      }
    }.start();
    
    new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(10000);
          server2.stop();
        } catch (Exception e) {
          e.printStackTrace();
        }
        
      }
    }.start();
    try {
      // Session<Message<Integer, Object>> session = client.getSession();
      for (int i = 0; i < 100; i++) {
        UserInfo user = new UserInfo();
        user.buildUserID(i + 100).buildUserName("test" + i);
        // logger.info("batch message : " + i);
        Object value = group.sendAndWait(user);
        logger.info("the value is : [{}]", value);
        Thread.sleep(1000);
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
      group.stop();
    } catch (Exception e) {
      logger.error("", e);
    }
  }

}
