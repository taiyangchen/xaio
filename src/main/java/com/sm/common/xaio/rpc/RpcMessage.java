package com.sm.common.xaio.rpc;

import com.sm.common.xaio.Message;

/**
 * 代表一个RPC消息，0:request;1:response;2:notify
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午3:55:32
 * @param <K>
 * @param <V>
 */
public abstract class RpcMessage<K, V> extends Message<K, V> {

  public RpcMessage(K type, V bean) {
    super(type, bean);
  }

  public RpcMessage(K type, V bean, int id) {
    super(type, bean, id);
  }

}
