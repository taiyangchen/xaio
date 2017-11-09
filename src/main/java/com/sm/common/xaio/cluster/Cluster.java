package com.sm.common.xaio.cluster;

import com.sm.common.xaio.Client;
import com.sm.common.xaio.bo.PeerInfo;

/**
 * 代表一个集群
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月27日 上午11:34:18
 * @param <T>
 */
public interface Cluster<T> {

  /**
   * 下一个连接 
   * 
   * @return 下一个连接
   */
  Client<T> next();

  /**
   * 加入新节点 
   * 
   * @param peer 新节点
   * @return 新的连接
   */
  Client<T> join(PeerInfo peer);

  /**
   * 移除节点
   * 
   * @param peer 节点
   * @return 是否成功
   */
  boolean leave(PeerInfo peer);

  /**
   * 注册事件监听器
   * 
   * @param listener 事件监听器 @see ClusterEventListener
   */
  void registerListener(ClusterEventListener<T> listener);

  /**
   * 获取节点数
   * 
   * @return 节点数
   */
  int size();

}
