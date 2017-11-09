/**
 * 
 */
package com.sm.common.xaio;

import com.sm.common.libs.able.Bootstrap;

/**
 * 保持连接
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月27日 下午7:20:33
 */
public interface Keepalive<E> extends Bootstrap {

  /**
   * 启动
   */
  @Override
  void start();

  /**
   * 停止
   */
  @Override
  void stop();

  void addSession(Session<E> session);

  void setTries(int tries);

}
