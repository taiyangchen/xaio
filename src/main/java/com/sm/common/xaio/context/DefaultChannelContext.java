/**
 * 
 */
package com.sm.common.xaio.context;

import java.nio.channels.AsynchronousSocketChannel;

import com.sm.common.xaio.Message;

/**
 * 默认的通信上下文实现
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月14日 下午9:21:42
 */
public class DefaultChannelContext extends ChannelContextSupport<Message<Integer, Object>> {

  public static DefaultChannelContext create(AsynchronousSocketChannel channel) {
    return new DefaultChannelContext(channel);
  }

  private DefaultChannelContext(AsynchronousSocketChannel channel) {
    this.channel = channel;
  }

}
