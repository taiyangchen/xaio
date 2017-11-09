/**
 * 
 */
package com.sm.common.xaio.session;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.protobuf.MessageLite;
import com.sm.common.xaio.RpcProtoType;
import com.sm.common.xaio.Session.Type;
import com.sm.common.xaio.proto.RpcData.RpcProto;

/**
 * creator for RpcProto
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月27日 下午4:55:31
 */
public abstract class RpcProtoCreator {

  /**
   * 请求ID生成器
   */
  private static final AtomicInteger uid = new AtomicInteger();

  /**
   * 创建内部proto的RPC对象
   * 
   * @param message protod的MessageLite @see MessageLite
   * @param type 客户端或服务端
   * @return 内部proto的RPC对象
   */
  public static RpcProto create(MessageLite message, Type type) {
    RpcProto rpcData = RpcProto.newBuilder().setBody(message.toByteString()).setRequestid(uid.incrementAndGet())
        .setType(type == Type.CLIENT ? RpcProtoType.CLIENT.value() : RpcProtoType.SERVER.value()).build();

    return rpcData;
  }

}
