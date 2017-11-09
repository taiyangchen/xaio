/**
 * 
 */
package com.sm.common.xaio.cluster;

import com.sm.common.libs.able.ResponseCallback;

/**
 * 广播接口
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月27日 上午11:32:11
 */
public interface Broadcaster {

  /**
   * 将消息对象广播给所有对等实体
   * 
   * @param bean 消息对象
   */
  void notifyAll(Object bean);

  /**
   * 将消息对象广播给所有对等实体
   * 
   * @param callback 回调接口
   * @param bean 消息对象
   */
  <T> void notifyAll(ResponseCallback<T> callback, Object bean);

  /**
   * 将消息发送给任一对等实体
   * 
   * @param bean 消息对象
   */
  void notifyAny(Object bean);

  /**
   * 将消息发送给任一对等实体
   * 
   * @param callback 回调接口
   * @param bean 消息对象
   */
  <T> void notifyAny(ResponseCallback<T> callback, Object bean);

  /**
   * 将消息对象广播给所有对等实体并且等待
   * 
   * @param bean 消息对象
   * @return 是否成功
   */
  boolean notifyAllAndWait(Object bean);

  /**
   * 将消息对象广播给所有对等实体并且等待
   * 
   * @param bean 消息对象
   * @return 是否成功
   */
  <T> boolean notifyAllAndWait(ResponseCallback<T> callback, Object bean);

  /**
   * 半同步发送，只要有任意连接响应即可
   * 
   * @param bean 消息对象
   * @return 返回结果
   */
  <T> T semiSyncNotifyAll(Object bean);

}
