/**
 * 
 */
package com.sm.common.xaio.zk;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.sm.common.libs.able.Bootstrap;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.util.CollectionUtil;
import com.sm.common.xaio.util.PathUtil;

/**
 * XzkClient
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 上午2:47:54
 */
public class XzkClient extends LoggerSupport implements Bootstrap, ZkNodeConstants {
  /**
   * 连接ZooKeeper实例的客户端，该客户端会在session loss和session expire时自动创建新的ZooKeeper实例进行重连
   */
  private ZkClient client;

  /**
   * ZooKeeper服务器地址，多台机器间用“,”分隔
   */
  private String servers;

  /**
   * session过期时间，默认1分钟
   */
  private int sessionTimeout = 60000;

  /**
   * 连接过期时间，默认1分钟
   */
  private int connectionTimeout = 60000;

  /**
   * 用来跟踪会话重新创建的次数
   */
  private AtomicInteger newSessionNum = new AtomicInteger();

  @Override
  public void start() {
    client = new ZkClient(servers, sessionTimeout, connectionTimeout, new StringZkSerializer());
    if (!client.exists(ROOT)) {
      client.createPersistent(ROOT);
    }
    // 订阅会话状态
    client.subscribeStateChanges(new IZkStateListener() {
      @Override
      public void handleStateChanged(KeeperState state) throws Exception {
        logger.info("zkclient state change=[{}]", state);
      }

      @Override
      public void handleNewSession() throws Exception {
        logger.info("new session created, recreated num=[{}]", newSessionNum.incrementAndGet());
      }

      @Override
      public void handleSessionEstablishmentError(Throwable error) throws Exception {
        logger.error("subscribeStateChanges error", error);
      }
    });
  }

  public void addDataChangesListener(String path, IZkDataListener listener) {
    client.subscribeDataChanges(path, listener);
  }

  public void addChildChangesListener(String path, IZkChildListener listener) {
    client.subscribeChildChanges(path, listener);
  }

  public void createPersistent(String path) {
    List<String> allPath = PathUtil.getLevelPaths(path);
    if (CollectionUtil.isEmpty(allPath)) {
      return;
    }

    for (String subPath : allPath) {
      if (!client.exists(subPath)) {
        client.createPersistent(subPath);
      }
    }

  }

  public void createPersistent(String path, Object data) {
    List<String> allPath = PathUtil.getLevelPaths(path);
    if (CollectionUtil.isEmpty(allPath)) {
      return;
    }

    for (String subPath : allPath) {
      if (!client.exists(subPath)) {
        client.createPersistent(subPath);
      }
    }

    client.writeData(path, data);
  }

  public List<String> getChildren(String path) {
    return client.getChildren(path);
  }

  public Object getData(String path) {
    return client.readData(path);
  }

  public boolean createEphemeral(String path) {
    if (!client.exists(path)) {
      client.createEphemeral(path);
      return true;
    }

    return false;
  }

  public void createEphemeral(String path, Object data) {
    if (!client.exists(path)) {
      client.createEphemeral(path, data);
    }

    client.writeData(path, data);
  }

  @Override
  public void stop() {
    client.close();
  }

  @Override
  public boolean isActive() {
    return client.exists(ROOT);
  }

  public void setServers(String servers) {
    this.servers = servers;
  }

  public void setSessionTimeout(int sessionTimeout) {
    this.sessionTimeout = sessionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

}
