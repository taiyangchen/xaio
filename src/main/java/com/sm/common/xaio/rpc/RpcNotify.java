package com.sm.common.xaio.rpc;

/**
 * RPC通知消息
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午8:24:27
 */
public class RpcNotify extends RpcMessage<Integer, MethodDesc> {

  /**
   * 是否回调
   */
  private boolean callback;

  /**
   * 是否全部通知
   */
  private boolean all;

  public RpcNotify(MethodDesc bean) {
    super(2, bean);
  }

  public RpcNotify(MethodDesc bean, boolean all) {
    super(2, bean);
    this.all = all;
  }

  public RpcNotify(MethodDesc bean, int id) {
    super(2, bean, id);
  }

  public RpcNotify(MethodDesc bean, int id, boolean all) {
    super(2, bean, id);
    this.all = all;
  }

  public String getMethodName() {
    return bean.getMethodName();
  }

  public Object[] getParams() {
    return bean.getParams();
  }

  public boolean isCallback() {
    return callback;
  }

  public void setCallback(boolean callback) {
    this.callback = callback;
  }

  public boolean isAll() {
    return all;
  }

  public void setAll(boolean all) {
    this.all = all;
  }

}
