package com.sm.common.xaio.rpc;

import com.sm.common.libs.core.ToStringSupport;

/**
 * 方法描述
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午2:21:07
 */
public class MethodDesc extends ToStringSupport {
  /**
   * 方法名称
   */
  private String methodName;

  /**
   * 方法参数
   */
  private Object[] params;

  public MethodDesc() {

  }

  public MethodDesc(String methodName, Object... params) {
    this.methodName = methodName;
    this.params = params;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public Object[] getParams() {
    return params;
  }

  public void setParams(Object[] params) {
    this.params = params;
  }

}
