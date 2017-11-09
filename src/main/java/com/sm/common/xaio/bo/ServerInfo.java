/**
 * 
 */
package com.sm.common.xaio.bo;

import com.sm.common.libs.core.ToStringSupport;
import com.sm.common.libs.util.CastUtil;
import com.sm.common.libs.util.ObjectUtil;

/**
 * ServerInfo
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月27日 下午6:53:56
 */
public class ServerInfo extends ToStringSupport implements Comparable<ServerInfo> {

  private PeerInfo peerInfo = new PeerInfo();

  private String version;

  private int weight = 1;

  private ServerCategory category = new ServerCategory();

  public ServerInfo() {

  }

  public ServerInfo(String ip, int port) {
    this(ip, port, null);
  }

  public ServerInfo(String ip, int port, String version) {
    peerInfo.setHostName(ip);
    peerInfo.setPort(port);
    this.version = version;
  }

  public ServerInfo(String ip, int port, int weight) {
    peerInfo.setHostName(ip);
    peerInfo.setPort(port);
    this.weight = weight;
  }

  public ServerInfo(String ip, int port, String version, int weight) {
    peerInfo.setHostName(ip);
    peerInfo.setPort(port);
    this.version = version;
    this.weight = weight;
  }

  @Override
  public int compareTo(ServerInfo o) {
    // must not null
    return peerInfo.compareTo(o.peerInfo);
  }

  @Override
  public int hashCode() {
    return peerInfo.hashCode();
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
    ServerInfo other = CastUtil.cast(obj);
    return peerInfo.equals(other.peerInfo);
  }

  public ServerCategory getCategory() {
    return category;
  }

  public void setCategory(ServerCategory category) {
    this.category = category;
  }

  public void setDomain(String domain) {
    this.category.setDomain(domain);
  }

  public void setGroup(String group) {
    this.category.setGroup(group);
  }

  public boolean isSameDomain(ServerInfo server) {
    return this.category.isSameDomain(server.getCategory());
  }

  public boolean isSameVersion(ServerInfo server) {
    return ObjectUtil.isEquals(this.version, server.version);
  }

  public String getDomain() {
    return this.category.getDomain();
  }

  public String getGroup() {
    return this.category.getGroup();
  }

  public String getHostName() {
    return peerInfo.getHostName();
  }

  public int getPort() {
    return peerInfo.getPort();
  }

  public int getPid() {
    return peerInfo.getPid();
  }

  public void setHostName(String hostName) {
    peerInfo.setHostName(hostName);
  }

  public void setPort(int port) {
    peerInfo.setPort(port);
  }

  public void setPid(int pid) {
    peerInfo.setPid(pid);
  }

  public PeerInfo getPeerInfo() {
    return peerInfo;
  }

  public void setPeerInfo(PeerInfo peerInfo) {
    this.peerInfo = peerInfo;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

}
