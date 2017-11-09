/**
 * 
 */
package com.sm.common.xaio.client;

import java.nio.channels.AsynchronousSocketChannel;

import com.sm.common.xaio.AsyncClient;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.context.DefaultChannelContext;
import com.sm.common.xaio.session.DefaultSession;

/**
 * 默认的异步通信客户端
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月9日 下午2:53:56
 */
public class DefaultAsyncClient extends AsyncChannelClientSupport<Message<Integer, Object>>
    implements AsyncClient<Message<Integer, Object>> {

  public DefaultAsyncClient() {

  }

  public DefaultAsyncClient(boolean autoCreateSession) {
    this.autoCreateSession = autoCreateSession;
  }

  @Override
  protected ChannelContext<Message<Integer, Object>> createChannelContext(AsynchronousSocketChannel channel) {
    return DefaultChannelContext.create(channel);
  }

  @Override
  protected Session<Message<Integer, Object>> newSession(ChannelContext<Message<Integer, Object>> channelContext) {
    return new DefaultSession(channelContext);
  }

}
