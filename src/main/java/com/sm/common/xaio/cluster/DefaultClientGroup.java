/**
 * 
 */
package com.sm.common.xaio.cluster;

import java.util.ArrayList;
import java.util.List;

import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.bus.BusSignalListener;
import com.sm.common.xaio.Client;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.bo.PeerInfo;
import com.sm.common.xaio.bo.ServerGroup;
import com.sm.common.xaio.client.DefaultAsyncClient;
import com.sm.common.xaio.subscribe.Subscribe;

/**
 * DefaultClientGroup
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 上午2:13:32
 */
public class DefaultClientGroup extends ClusterSupport<Message<Integer, Object>>
    implements BusSignalListener<ServerGroup> {

  private Subscribe subscribe;

  private BusRegistry<ServerGroup> busRegistry;

  @Override
  public void start() throws Exception {
    List<PeerInfo> peers = subscribe.subscribe(category, version);
    for (PeerInfo peer : peers) {
      Client<Message<Integer, Object>> client = create(peer);
      clientMap.putIfAbsent(peer, client);
    }
    snapshot = new ArrayList<>(clientMap.keySet());
    updateRoutes();

    super.start();
  }

  @Override
  protected Client<Message<Integer, Object>> createClient() {
    DefaultAsyncClient client = new DefaultAsyncClient(true);
    client.setCodec(codec);
    client.setReceiver(receiver);
    return client;
  }

  public void setSubscribe(Subscribe subscribe) {
    this.subscribe = subscribe;
  }

  @Override
  public void signalFired(ServerGroup signal) {
    if (category.equals(signal.getCategory()) && version.equals(signal.getVersion())) {
      logger.info("receiver server group changed which=[{}]", category);
      refreshRoute(signal.getList());
    }
  }

  public void setBusRegistry(BusRegistry<ServerGroup> busRegistry) {
    this.busRegistry = busRegistry;
    this.busRegistry.getSignalManager().bind(ServerGroup.class, this);
  }

}
