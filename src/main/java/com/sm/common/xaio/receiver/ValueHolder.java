package com.sm.common.xaio.receiver;

import com.sm.common.libs.core.ToStringSupport;

/**
 * Value Holder
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 上午11:51:08
 */
public class ValueHolder extends ToStringSupport {

  // message
  private Object msg;

  // receiver (maybe)
  private Object value;

  public ValueHolder(Object msg, Object value) {
    this.msg = msg;
    this.value = value;
  }

  public Object getMsg() {
    return msg;
  }

  public Object getValue() {
    return value;
  }

}
