/**
 * 
 */
package com.sm.common.xaio.subscribe;

import java.util.List;

import com.sm.common.xaio.bo.PeerInfo;
import com.sm.common.xaio.bo.ServerCategory;

/**
 * Subscribe
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 上午2:35:16
 */
public interface Subscribe {

  // get current and update change
  List<PeerInfo> subscribe(ServerCategory category);

  List<PeerInfo> subscribe(ServerCategory category, String version);

  void addListener(ServerCategory category, SubscribeListener listener);

  void addListener(ServerCategory category, String version, SubscribeListener listener);

}
