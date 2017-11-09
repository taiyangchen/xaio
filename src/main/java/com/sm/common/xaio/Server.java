package com.sm.common.xaio;

import com.sm.common.libs.able.Bootstrap;
import com.sm.common.xaio.bo.PeerInfo;

/**
 * 网络传输服务端
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月11日 下午3:44:52
 */
public interface Server extends Bootstrap {

  /**
   * 获取启动时间
   * 
   * @return 启动时间
   */
  long getStartTime();
  
  void setMaxSessions(int maxSession);
  
  PeerInfo getPeer();

}
