/**
 * 
 */
package com.sm.common.xaio;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sm.common.libs.codec.MessageCodec;

/**
 * 传输通道上下文
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月14日 下午5:49:44
 */
public interface ChannelContext<E> {

  /**
   * 设置参数
   * 
   * @param key 参数键
   * @param value 参数值
   * @return this
   */
  <T> ChannelContext<E> set(Object key, T value);

  /**
   * 移除参数
   * 
   * @param key 参数键
   * @return this
   */
  ChannelContext<E> remove(Object key);

  /**
   * 获取参数值
   * 
   * @param key 参数键
   * @return 数值
   */
  <T> T get(Object key);

  /**
   * 返回异步连接通道
   * 
   * @return 异步连接通道
   */
  AsynchronousSocketChannel channel();

  /**
   * 设置连接通道
   * 
   * @param channel 异步连接通道
   * @return this
   */
  ChannelContext<E> channel(AsynchronousSocketChannel channel);

  /**
   * 释放资源
   */
  void release();

  /**
   * 关闭异步连接通道
   */
  void closeChannel();

  /**
   * 压入消息对象
   * 
   * @param message 消息对象
   * @return 是否成功
   */
  boolean push(E message);

  /**
   * 压入消息对象
   * 
   * @param message 消息对象
   */
  void put(E message);

  /**
   * 获取最早的消息对象
   * 
   * @return 最早的消息对象
   */
  E poll();

  /**
   * 获取最早的消息对象
   * 
   * @param timeout 超时时间
   * @param unit 超时时间单位 @see TimeUnit
   * @return 最早的消息对象
   */
  E poll(long timeout, TimeUnit unit);

  /**
   * 是否有消息对象
   * 
   * @return 是否有消息对象
   */
  boolean hasMessage();

  /**
   * 返回编解码器
   * 
   * @return 编解码器 @see MessageCodec
   */
  MessageCodec codec();

  /**
   * 设置编解码器
   * 
   * @param codec 编解码器 MessageCodec
   * @return this
   */
  ChannelContext<E> codec(MessageCodec codec);

  /**
   * 返回会话
   * 
   * @return 会话 @see Session
   */
  Session<E> session();

  /**
   * 设置会话
   * 
   * @param session 会话 @see Session
   * @return this
   */
  ChannelContext<E> session(Session<E> session);

  /**
   * 设置会话管理 FIXME
   * 
   * @param sessionManager 会话管理 @see SessionManager
   * @return this
   */
  ChannelContext<E> sessionManager(SessionManager<E> sessionManager);

  void removeSession();

  ChannelContext<E> setPendings(BlockingQueue<E> pendings);

}
