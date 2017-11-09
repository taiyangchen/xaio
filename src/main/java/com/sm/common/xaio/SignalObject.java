/**
 * 
 */
package com.sm.common.xaio;

import com.sm.common.libs.core.ToStringSupport;

/**
 * SignalObject
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月23日 下午5:58:22
 */
public class SignalObject<E> extends ToStringSupport {

  private final Throwable exc;

  private final E message;

  private SignalType type;

  public SignalObject(Throwable exc, E message, SignalType type) {
    this.exc = exc;
    this.message = message;
    this.type = type;
  }

  public Throwable getExc() {
    return exc;
  }

  public E getMessage() {
    return message;
  }

  public SignalType getType() {
    return type;
  }


}
