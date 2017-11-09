/**
 * 
 */
package com.sm.common.xaio.rpc;

import com.sm.common.libs.core.ToStringSupport;

/**
 * RpcResult
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年9月12日 上午12:00:39
 */
public class RpcResult extends ToStringSupport {

  private Object result;

  private long runtime;

  private Exception exception;

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }

  public long getRuntime() {
    return runtime;
  }

  public void setRuntime(long runtime) {
    this.runtime = runtime;
  }

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }

}
