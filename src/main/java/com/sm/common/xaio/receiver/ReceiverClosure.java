package com.sm.common.xaio.receiver;

import com.sm.common.libs.able.Closure;
import com.sm.common.libs.able.Receiver;

/**
 * Receiver Closure
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 上午11:56:49
 */
public class ReceiverClosure implements Receiver<ValueHolder> {

  private Receiver<Object> receiver;

  private Closure nextClosure;

  @Override
  public void messageReceived(ValueHolder msg) {
    receiver.messageReceived(msg.getMsg());

    if (nextClosure != null) {
      nextClosure.execute(msg);
    }

  }

  public void setReceiver(Receiver<Object> receiver) {
    this.receiver = receiver;
  }

  public void setNextClosure(Closure nextClosure) {
    this.nextClosure = nextClosure;
  }

}
