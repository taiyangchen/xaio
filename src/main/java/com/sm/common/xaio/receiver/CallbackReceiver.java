package com.sm.common.xaio.receiver;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.able.ResponseCallback;

/**
 * 回调信息接收
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午12:01:06
 */
public class CallbackReceiver implements Receiver<Object> {

  /**
   * 回调接口
   */
  private ResponseCallback<Object> callback;

  public CallbackReceiver(ResponseCallback<Object> callback) {
    this.callback = callback;
  }

  // FIXME
  @Override
  public void messageReceived(Object msg) {
    try {
      callback.onSuccess(msg);
    } catch (Exception e) {
      callback.onException(e);
    }
  }
}
