/**
 * 
 */
package com.sm.common.xaio.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sm.common.libs.util.CastUtil;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.Session;

/**
 * 会话相关工具类
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午9:06:27
 */
public abstract class SessionUtil {

  /**
   * 消息与会话的绑定，用于解耦
   */
  private static final ConcurrentMap<Object, Session<?>> context = new ConcurrentHashMap<>();

  /**
   * 将消息绑定到会话上
   * 
   * @param message 消息 @see Message
   * @param session 会话 @see Session
   * @return 消息体 @see Message
   */
  public static Message<Integer, Object> bind(Message<Integer, Object> message,
      Session<Message<Integer, Object>> session) {
    context.put(message, session);
    return message;
  }

  /**
   * 将消息绑定到会话上
   * 
   * @param message 消息
   * @param session 会话 @see Session
   * @return 消息体
   */
  public static <E> E bind(E message, Session<E> session) {
    context.put(message, session);
    return message;
  }

  /**
   * 通过消息获取绑定的会话
   * 
   * @param message 消息 @see Message
   * @return 绑定的会话 @see Session
   */
  public static <E> Session<E> getSession(Message<?, ?> message) {
    Session<E> session = CastUtil.cast(context.remove(message));
    return session;
  }

  /**
   * 通过消息获取绑定的会话
   * 
   * @param message 消息 @see Message
   * @return 绑定的会话 @see Session
   */
  public static <E> Session<E> getSession(Object message) {
    Session<E> session = CastUtil.cast(context.remove(message));
    return session;
  }

  public static void unbind(Object message) {
    context.remove(message);
  }

}
