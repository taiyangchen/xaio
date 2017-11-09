/**
 * 
 */
package com.sm.common.xaio.context;

import java.nio.channels.AsynchronousSocketChannel;

import com.google.protobuf.Message;

/**
 * proto通信上下文实现
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月14日 下午9:21:42
 */
public class ProtoChannelContext extends ChannelContextSupport<Message> {

  /**
   * 创建proto的通道上下文
   * 
   * @param channel 异步网络通道 @see AsynchronousSocketChannel
   * @return proto的通道上下文 @see ProtoChannelContext
   */
  public static ProtoChannelContext create(AsynchronousSocketChannel channel) {
    return new ProtoChannelContext(channel);
  }

  private ProtoChannelContext(AsynchronousSocketChannel channel) {
    this.channel = channel;
  }

}
