package com.sm.common.xaio.rpc;

/**
 * RpcRequest
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月29日 上午11:11:07
 */
public class RpcRequest extends RpcMessage<Integer, MethodDesc> {

  public RpcRequest(MethodDesc bean) {
    super(0, bean);
  }

  public RpcRequest(MethodDesc bean, int id) {
    super(0, bean, id);
  }

  public String getMethodName() {
    return bean.getMethodName();
  }

  public Object[] getParams() {
    return bean.getParams();
  }

}
