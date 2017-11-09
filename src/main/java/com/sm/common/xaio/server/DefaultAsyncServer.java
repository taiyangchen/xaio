/**
 * 
 */
package com.sm.common.xaio.server;

import java.nio.channels.AsynchronousSocketChannel;

import com.sm.common.xaio.AsyncServer;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.context.DefaultChannelContext;
import com.sm.common.xaio.session.DefaultSession;


/**
 * 默认的异步通信服务端
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月11日 下午3:45:26
 */
public class DefaultAsyncServer extends AsyncChannelServerSupport<Message<Integer, Object>> implements AsyncServer {

  @Override
  protected ChannelContext<Message<Integer, Object>> createChannelContext(AsynchronousSocketChannel channel) {
    return DefaultChannelContext.create(channel);
  }

  @Override
  protected Session<Message<Integer, Object>> newSession(ChannelContext<Message<Integer, Object>> channelContext) {
    return new DefaultSession(channelContext);
  }

}
