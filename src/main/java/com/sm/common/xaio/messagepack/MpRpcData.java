/**
 * 
 */
package com.sm.common.xaio.messagepack;

import com.sm.common.libs.core.ToStringSupport;

/**
 * MpRpcData
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月23日 上午2:21:14
 */
public class MpRpcData extends ToStringSupport {

  private int requestId;

  private int type;

  private String serviceName;

  private String methodName;

  private byte[] body;

  public int getRequestId() {
    return requestId;
  }

  public void setRequestId(int requestId) {
    this.requestId = requestId;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public byte[] getBody() {
    return body;
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

}
