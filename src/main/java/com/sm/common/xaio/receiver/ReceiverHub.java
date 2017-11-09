package com.sm.common.xaio.receiver;

import com.sm.common.libs.able.Receiver;

/**
 * 接收枢纽，将不同的对象分发到不同的选择器上
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 上午11:57:12
 */
public class ReceiverHub implements Receiver<ValueHolder> {
  /**
   * 消息接收器
   */
  private ReceiverSelector selector;

  @Override
  public void messageReceived(ValueHolder msg) {
    Receiver<Object> receiver = selector.select(msg);
    receiver.messageReceived(msg);
  }

  public void setSelector(ReceiverSelector selector) {
    this.selector = selector;
  }

}
