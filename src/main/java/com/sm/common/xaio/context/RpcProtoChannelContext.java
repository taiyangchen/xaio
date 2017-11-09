/**
 * 
 */
package com.sm.common.xaio.context;

import java.nio.channels.AsynchronousSocketChannel;

import com.sm.common.xaio.proto.RpcData;

/**
 * proto通信上下文实现
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月14日 下午9:21:42
 */
public class RpcProtoChannelContext extends ChannelContextSupport<RpcData.RpcProto> {

  /**
   * 创建proto通信上下文
   * 
   * @param channel 异步网络通道 @see AsynchronousSocketChannel
   * @return roto通信上下文
   */
  public static RpcProtoChannelContext create(AsynchronousSocketChannel channel) {
    return new RpcProtoChannelContext(channel);
  }

  private RpcProtoChannelContext(AsynchronousSocketChannel channel) {
    this.channel = channel;
  }

}
