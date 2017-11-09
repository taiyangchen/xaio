/**
 * 
 */
package com.sm.common.xaio;

import com.sm.common.libs.able.Valuable;

/**
 * 内部RpcProto的协议，用来区分客户端和服务端
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月30日 下午5:08:36
 */
public enum RpcProtoType implements Valuable<Integer> {

  CLIENT(1), SERVER(8);

  private int value;

  RpcProtoType(int value) {
    this.value = value;
  }

  @Override
  public Integer value() {
    return value;
  }

}
