/**
 * 
 */
package com.sm.common.xaio.subscribe;

import com.sm.common.xaio.bo.ServerGroup;

/**
 * SubscribeListener
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年8月24日 上午12:42:56
 */
public interface SubscribeListener {

  void onChanged(ServerGroup serverGroup);

}
