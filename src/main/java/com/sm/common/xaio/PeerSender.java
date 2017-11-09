/**
 * 
 */
package com.sm.common.xaio;

import java.util.concurrent.TimeUnit;

import com.sm.common.libs.able.ResponseCallback;

/**
 * 对等实体消息发送
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月9日 下午2:04:14
 */
public interface PeerSender {

  /**
   * 发送消息
   * 
   * @param msg 消息对象
   */
  void send(Object bean);

  /**
   * 发送消息实体后执行异步回调
   * 
   * @param callback 回调接口 @see ResponseCallback
   * @param bean 消息对象
   */
  <T> void send(ResponseCallback<T> callback, Object bean);

  /**
   * 发送消息实体后等待响应，需有默认超时时间
   * 
   * @param bean 消息对象
   * @return 对等实体返回结果
   * @throws TransportException
   */
  <T> T sendAndWait(Object bean) throws TransportException;

  /**
   * 发送消息实体后等待响应
   * 
   * @param bean 消息对象
   * @param duration 等待时间
   * @param unit 时间单位
   * @throws TransportException
   */
  <T> T sendAndWait(Object bean, long duration, TimeUnit unit) throws TransportException;

}
