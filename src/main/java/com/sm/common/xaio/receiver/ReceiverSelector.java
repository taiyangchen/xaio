package com.sm.common.xaio.receiver;

import com.sm.common.libs.able.Receiver;

/**
 * 消息接收选择器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 上午11:50:41
 */
public interface ReceiverSelector {

  /**
   * 通过接收消息<code>message</code>选择消息接收器
   * 
   * @param message 接收消息
   * @return 消息接收器 @see Receiver
   */
  Receiver<Object> select(ValueHolder message);

}
