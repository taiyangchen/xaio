/**
 * 
 */
package com.sm.common.xaio.bo;

import java.net.InetSocketAddress;

import org.apache.commons.lang3.StringUtils;

import com.sm.common.libs.util.CastUtil;
import com.sm.common.libs.util.IpUtil;
import com.sm.common.libs.util.JmxUtil;

/**
 * 代表个对等实体
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月9日 下午2:38:49
 */
public class PeerInfo implements Comparable<PeerInfo> {

  private String hostName;
  private int port;
  private int pid;// option

  public PeerInfo() {
    // this(null, -1);
  }

  public PeerInfo(int pid) {
    this(null, -1, pid);
  }

  public PeerInfo(InetSocketAddress localAddress, int pid) {
    this(localAddress.getHostName(), localAddress.getPort(), pid);
  }

  public PeerInfo(InetSocketAddress localAddress) {
    this(localAddress.getHostName(), localAddress.getPort());
  }

  public PeerInfo(String hostName, int port) {
    this.hostName = hostName;
    this.port = port;
    this.pid = JmxUtil.getPid();
  }

  public PeerInfo(String hostName, int port, int pid) {
    this.hostName = hostName;
    this.port = port;
    this.pid = pid;
  }

  @Override
  public String toString() {
    if (pid <= 0) {
      return getName();
    }

    return getName() + "[" + getPid() + "]";
  }

  @Override
  public int compareTo(PeerInfo o) {
    int rslt = this.hostName.compareTo(o.hostName);
    if (rslt != 0) {
      return rslt;
    }

    return this.port - o.port;
  }

  @Override
  public int hashCode() {
    return (hostName + ":" + port).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    // 到此处类型必相同
    PeerInfo other = CastUtil.cast(obj);
    return hostName.equals(other.hostName) && port == other.port;
  }

  public String getName() {
    return getHostName() + ":" + getPort();
  }

  public String getHostName() {
    return hostName;
  }

  public int getPort() {
    return port;
  }

  public int getPid() {
    return pid;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setPid(int pid) {
    this.pid = pid;
  }

  public InetSocketAddress getAddress() {
    if (StringUtils.isEmpty(hostName)) {
      hostName = IpUtil.getLocalV4Ip();
    }
    return new InetSocketAddress(hostName, port);
  }

}
