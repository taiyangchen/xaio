/**
 * 
 */
package com.sm.common.xaio.client;

import java.nio.channels.AsynchronousSocketChannel;

import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.codec.ProtoCodec;
import com.sm.common.xaio.context.RpcProtoChannelContext;
import com.sm.common.xaio.proto.RpcData.RpcProto;
import com.sm.common.xaio.session.RpcProtoSession;

/**
 * proto的异步通信客户端
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月9日 下午2:53:56
 */
public class RpcProtoAsyncClient extends AsyncChannelClientSupport<RpcProto> {

  /**
   * 内部设置的proto RPC 协议格式
   */
  public RpcProtoAsyncClient() {
    codec = new ProtoCodec(RpcProto.getDefaultInstance());
  }

  /**
   * 内部设置的proto RPC 协议格式
   * 
   * @param autoCreateSession 是否自动创建<code>Session</code>
   */
  public RpcProtoAsyncClient(boolean autoCreateSession) {
    codec = new ProtoCodec(RpcProto.getDefaultInstance());
    this.autoCreateSession = autoCreateSession;
  }

  @Override
  protected ChannelContext<RpcProto> createChannelContext(AsynchronousSocketChannel channel) {
    return RpcProtoChannelContext.create(channel);
  }

  @Override
  protected Session<RpcProto> newSession(ChannelContext<RpcProto> channelContext) {
    return new RpcProtoSession(channelContext);
  }

}
