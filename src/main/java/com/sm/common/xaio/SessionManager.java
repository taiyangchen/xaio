/**
 * 
 */
package com.sm.common.xaio;

import java.util.Collection;
import java.util.List;

import com.sm.common.libs.able.Stopable;

/**
 * 会话管理器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月11日 下午4:16:35
 */
public interface SessionManager<E> extends Stopable {

  /**
   * 添加会话
   * 
   * @param session 会话 @see <code>Session</code>
   */
  void addSession(Session<E> session);
  
  void addSessions(Collection<Session<E>> sessions);

  /**
   * 移除会话
   * 
   * @param session 会话 @see <code>Session</code>
   */
  void removeSession(Session<E> session);

  /**
   * 获取会话列表 @see Session
   * 
   * @return 会话列表
   */
  List<Session<E>> getSessions();

  /**
   * 获取会话 @see Session
   * 
   * @return 会话
   */
  Session<E> getSession();

  /**
   * 获取会话 @see Session
   * 
   * @param index 索引号
   * @return 会话
   */
  Session<E> getSession(int index);

  /**
   * 设置最大会话数
   * 
   * @param maxSession 最大会话数
   */
  void setMaxSession(int maxSession);

  /**
   * 获取最大会话数
   * 
   * @return 最大会话数
   */
  int getMaxSession();

  /**
   * 会话数是否已满
   * 
   * @return 会话数是否已满
   */
  boolean isFull();
  
  int size();

}
