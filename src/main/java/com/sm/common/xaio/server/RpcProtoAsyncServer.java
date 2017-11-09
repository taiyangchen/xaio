/**
 * 
 */
package com.sm.common.xaio.server;

import java.nio.channels.AsynchronousSocketChannel;

import com.sm.common.xaio.AsyncServer;
import com.sm.common.xaio.ChannelContext;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.codec.ProtoCodec;
import com.sm.common.xaio.context.RpcProtoChannelContext;
import com.sm.common.xaio.proto.RpcData.RpcProto;
import com.sm.common.xaio.session.RpcProtoSession;

/**
 * proto异步通信服务端
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月11日 下午3:45:26
 */
public class RpcProtoAsyncServer extends AsyncChannelServerSupport<RpcProto> implements AsyncServer {

  /**
   * 内部设置的proto RPC 协议格式
   */
  public RpcProtoAsyncServer() {
    codec = new ProtoCodec(RpcProto.getDefaultInstance());
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
