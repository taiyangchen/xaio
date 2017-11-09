package com.sm.common.xaio.closure;

import com.sm.common.libs.able.Closure;
import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.core.ResponseFuture;
import com.sm.common.libs.util.CastUtil;
import com.sm.common.xaio.receiver.ValueHolder;

/**
 * 处理异步响应对象的<code>Closure</code>
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午1:50:19
 */
public class FutureClosure implements Closure, Receiver<Object> {

  @Override
  public void execute(Object... input) {
    ValueHolder holder = (ValueHolder) input[0];

    ResponseFuture<Object> tf = CastUtil.cast(holder.getValue());
    tf.set(holder.getMsg());
  }

  @Override
  public void messageReceived(Object msg) {
    execute(msg);
  }

}
