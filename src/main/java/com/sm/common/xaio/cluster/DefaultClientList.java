/**
 * 
 */
package com.sm.common.xaio.cluster;

import java.util.List;

import com.sm.common.libs.collection.ArrayHashMap;
import com.sm.common.libs.collection.ListMap;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.Client;

/**
 * DefaultClientGroup
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月30日 下午4:39:07
 */
public class DefaultClientList<E> extends LoggerSupport implements ClientList<E> {

  private ListMap<Object, Client<E>> clients = new ArrayHashMap<>();

  private String name;

  public DefaultClientList(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public List<Client<E>> getClients() {
    return clients.valueList();
  }

  @Override
  public synchronized boolean addClient(Object key, Client<E> client) throws Exception {
    Client<E> oldClient = clients.put(key, client);
    if (oldClient != null) {
      oldClient.stop();
    }

    client.start();
    boolean ret = client.isActive();
    if (!ret) {
      logger.warn("add client connected to {} fail!,removed", client.getPeer().getAddress());
      client.stop();
      clients.remove(key);
      return ret;
    }

    logger.info("add client connected to {} success!", client.getPeer().getAddress());
    return ret;
  }

  @Override
  public synchronized boolean removeClient(Object key) throws Exception {
    Client<E> client = clients.remove(key);
    if (client == null) {
      return false;
    }

    client.stop();
    logger.warn("client=[{}] stopped", client);
    return !client.isActive();
  }

  @Override
  public Client<E> getClient(Object key) {
    return clients.get(key);
  }

  @Override
  public synchronized void stop() {
    for (Client<E> client : clients.valueList()) {
      try {
        client.stop();
      } catch (Exception e) {
        logger.error("close client error,peer=" + client.getPeer(), e);
      }
    }
    clients.clear();
  }

  @Override
  public int size() {
    return clients.size();
  }

}
