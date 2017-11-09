/**
 * 
 */
package com.sm.common.xaio;

import java.nio.channels.AsynchronousSocketChannel;

import com.sm.common.libs.able.Bootstrap;
import com.sm.common.libs.able.Receiver;

/**
 * 代表一个连接会话
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月11日 上午1:55:28
 */
public interface Session<E> extends Receiver<E>, PeerSender, Bootstrap {

  /**
   * 会话ID
   * 
   * @return 会话ID
   */
  String getId();

  ConnectedInfo getConnectedInfo();

  /**
   * 设置消息接收器
   * 
   * @param receiver 消息接收器
   */
  void setReceiver(Receiver<E> receiver);

  // void setQueueSize(int messageCached);

  /**
   * 设置发送超时时间，单位毫秒
   * 
   * @param sendTimeout 发送超时时间
   */
  void setSendTimeout(int sendTimeout);

  // void setWaitTimeout(long waitTimeout);

  @Override
  void start();

  @Override
  void stop();

  /**
   * 会话类型 @see Type
   * 
   * @return 会话类型
   */
  Type getType();

  /**
   * 设置会话类型
   * 
   * @param type 会话类型
   */
  void setType(Type type);

  /**
   * 重置连接通道，此方法一般在当前通道失效时使用
   * 
   * @param channel 连接通道 @see AsynchronousSocketChannel
   */
  void resetChannel(AsynchronousSocketChannel channel);

  /**
   * 会话类型
   * 
   * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
   * @version create on 2015-7-4 下午10:31:59
   */
  enum Type {
    /**
     * 代表客户端
     */
    CLIENT,
    /**
     * 代表服务端
     */
    SERVER;
  }

}
