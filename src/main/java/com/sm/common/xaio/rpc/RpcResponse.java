package com.sm.common.xaio.rpc;

/**
 * RPC响应消息
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午3:56:03
 * @param <T>
 */
public class RpcResponse<T> extends RpcMessage<Integer, T> {

  private boolean success;

  private Throwable error;

  public RpcResponse(T bean, int id) {
    super(1, bean, id);
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public Throwable getError() {
    return error;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  public void setId(int id) {
    this.id = id;
  }

}
