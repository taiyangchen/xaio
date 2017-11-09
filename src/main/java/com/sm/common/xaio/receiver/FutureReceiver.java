package com.sm.common.xaio.receiver;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.core.ResponseFuture;

/**
 * <code>ResponseFuture</code>信息接收
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午12:01:20
 */
public class FutureReceiver implements Receiver<Object> {

  private ResponseFuture<Object> future;

  public FutureReceiver(ResponseFuture<Object> future) {
    this.future = future;
  }

  @Override
  public void messageReceived(Object msg) {
    future.set(msg);
  }

}
