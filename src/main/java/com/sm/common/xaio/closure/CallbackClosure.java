package com.sm.common.xaio.closure;

import com.sm.common.libs.able.Closure;
import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.libs.util.CastUtil;
import com.sm.common.xaio.receiver.ValueHolder;

/**
 * 处理回调接口的<code>Closure</code>
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午12:06:44
 */
public class CallbackClosure implements Closure, Receiver<Object> {

  @Override
  public void execute(Object... input) {
    ValueHolder holder = (ValueHolder) input[0];

    ResponseCallback<Object> rc = CastUtil.cast(holder.getValue());
    Object msg = holder.getMsg();
    try {
      rc.onSuccess(msg);
    } catch (Exception e) {
      rc.onException(e);
    }
  }

  @Override
  public void messageReceived(Object msg) {
    execute(msg);
  }

}
