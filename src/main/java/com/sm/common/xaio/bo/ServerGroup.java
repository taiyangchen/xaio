/**
 * 
 */
package com.sm.common.xaio.bo;

import java.util.ArrayList;
import java.util.List;

import com.sm.common.libs.core.ToStringSupport;

/**
 * ServerGroup
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年7月5日 下午1:07:48
 */
public class ServerGroup extends ToStringSupport {

  private ServerCategory category = new ServerCategory();

  private List<PeerInfo> list = new ArrayList<>();

  private String version;

  public ServerGroup add(PeerInfo peer) {
    list.add(peer);
    return this;
  }

  public List<PeerInfo> getList() {
    return list;
  }

  public ServerCategory getCategory() {
    return category;
  }

  public void setCategory(ServerCategory category) {
    this.category = category;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

}
