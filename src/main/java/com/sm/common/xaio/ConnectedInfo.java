/**
 * 
 */
package com.sm.common.xaio;

import java.net.SocketAddress;

import com.sm.common.libs.core.ToStringSupport;

/**
 * ConnectedInfo
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月7日 下午4:46:27
 */
public class ConnectedInfo extends ToStringSupport {

  private SocketAddress localAddress;

  private SocketAddress remoteAddress;

//  private int pid;

  public SocketAddress getLocalAddress() {
    return localAddress;
  }

  public void setLocalAddress(SocketAddress localAddress) {
    this.localAddress = localAddress;
  }

  public SocketAddress getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(SocketAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  // public int getPid() {
  // return pid;
  // }
  //
  // public void setPid(int pid) {
  // this.pid = pid;
  // }

}
