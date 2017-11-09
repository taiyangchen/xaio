package com.sm.common.xaio.cluster;

import java.util.EventListener;

import com.sm.common.xaio.Client;
import com.sm.common.xaio.bo.PeerInfo;

/**
 * 集群事件的监听器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月27日 上午11:33:30
 * @param <T>
 */
public interface ClusterEventListener<T> extends EventListener {

  void joinCompleted(Client<T> client);

  void leaveCompleted(PeerInfo peer);

}
