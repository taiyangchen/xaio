package com.sm.common.xaio.receiver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.ClassUtils;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.libs.core.ResponseFuture;
import com.sm.common.xaio.closure.CallbackClosure;
import com.sm.common.xaio.closure.FutureClosure;

/**
 * 支持各种类型的消息接收选择器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 上午11:57:36
 */
public class AnyReceiverSelector implements ReceiverSelector {

  /**
   * 消息接收器 FIXME to Receiver[]
   */
  private Receiver<Object> receiver;
  /**
   * 选择器
   */
  private final ConcurrentMap<Class<?>, Receiver<Object>> selector = new ConcurrentHashMap<>();

  public AnyReceiverSelector() {
    register();
  }

  /**
   * 注册接收器
   */
  private void register() {
    selector.put(ResponseFuture.class, new FutureClosure());
    selector.put(ResponseCallback.class, new CallbackClosure());
    // selector.put(Signal.class, receiver);
  }

  @Override
  public Receiver<Object> select(ValueHolder message) {
    Object value = message.getValue();
    if (value == null) {
      return receiver;
    }

    Class<?> clazz = value.getClass();

    Receiver<Object> receiver = selector.get(clazz);
    if (receiver != null) {
      return receiver;
    }

    return assignAndSet(clazz);
  }

  /**
   * 分配选择器
   * 
   * @param fromClass Class类型
   * @return 消息接收器 @see Receiver
   */
  private Receiver<Object> assignAndSet(Class<?> fromClass) {
    for (Map.Entry<Class<?>, Receiver<Object>> entry : selector.entrySet()) {
      Class<?> clazz = entry.getKey();

      Receiver<Object> receiver = entry.getValue();
      // FIXME
      if (ClassUtils.isAssignable(fromClass, clazz)) {
        Receiver<Object> ret = selector.putIfAbsent(fromClass, receiver);

        return ret;
      }
    }

    selector.putIfAbsent(fromClass, receiver);
    return receiver;

  }

  public void setReceiver(Receiver<Object> receiver) {
    this.receiver = receiver;
  }
}
