/**
 * 
 */
package com.sm.common.xaio.receiver;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.core.LoggerSupport;

/**
 * 调试用
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月11日 上午11:20:38
 */
public class DebugReceiver<T> extends LoggerSupport implements Receiver<T> {

  @Override
  public void messageReceived(T message) {
    logger.debug("received message:{}", message);
  }

}
