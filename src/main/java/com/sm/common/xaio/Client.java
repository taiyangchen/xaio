/**
 * 
 */
package com.sm.common.xaio;

import com.sm.common.libs.able.Bootstrap;
import com.sm.common.xaio.bo.PeerInfo;

/**
 * 网络传输客户端
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月9日 下午2:37:17
 */
public interface Client<E> extends Bootstrap, PeerSender {

  /**
   * 获取启动时间
   * 
   * @return
   */
  long getStartTime();

  /**
   * 获取会话
   * 
   * @return 会话 @see Session
   * @throws Exception
   */
  Session<E> getSession() throws Exception;

  /**
   * 设置连接服务器名称
   * 
   * @param hostname 服务器名称
   */
  void setHostName(String hostname);

  /**
   * 设置连接服务器端口
   * 
   * @param port 服务器端口
   */
  void setPort(int port);

  void setMaxSessions(int maxSessions);

  PeerInfo getPeer();

  int sessionSize();

}
