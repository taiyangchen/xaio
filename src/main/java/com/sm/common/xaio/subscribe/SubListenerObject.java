/**
 * 
 */
package com.sm.common.xaio.subscribe;

import com.sm.common.libs.core.ToStringSupport;
import com.sm.common.xaio.bo.ServerCategory;

/**
 * SubListenerObject
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年8月24日 上午12:45:46
 */
public class SubListenerObject extends ToStringSupport {

  private SubscribeListener listener;

  private ServerCategory category;

  private String version;

  public SubscribeListener getListener() {
    return listener;
  }

  public void setListener(SubscribeListener listener) {
    this.listener = listener;
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
