/**
 * 
 */
package com.sm.common.xaio.cluster;

import java.util.List;

import com.sm.common.libs.able.Stopable;
import com.sm.common.xaio.Client;

/**
 * ClientGroup
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月30日 下午4:35:33
 */
public interface ClientList<E> extends Stopable {

  String name();

  List<Client<E>> getClients();

  boolean addClient(Object key, Client<E> client) throws Exception;

  boolean removeClient(Object key) throws Exception;

  Client<E> getClient(Object key);

  int size();

}
