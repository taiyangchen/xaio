/**
 * 
 */
package com.sm.common.xaio.session;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.SessionManager;

/**
 * 默认的会话管理实现 需要做更多的事情
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月11日 下午4:33:15
 */
public class DefaultSessionManager<E> extends LoggerSupport implements SessionManager<E> {

  /**
   * 会话列表 @see Session <p> use LockFreeList??
   */
  private List<Session<E>> sessionStore = new CopyOnWriteArrayList<>();

  /**
   * 会话ID
   */
  private AtomicInteger sessionIdx = new AtomicInteger(0);

  /**
   * 最大会话数
   */
  private int maxSession = 1;

  public DefaultSessionManager() {

  }

  public DefaultSessionManager(int maxSession) {
    this.maxSession = maxSession;
  }

  public static <E> SessionManager<E> createSessionManager(int maxSession) {
    return new DefaultSessionManager<E>(maxSession);
  }

  @Override
  public void addSession(Session<E> session) {
    if (sessionStore.size() < maxSession) {
      sessionStore.add(session);
    }
  }

  @Override
  public void addSessions(Collection<Session<E>> sessions) {
    if (sessionStore.size() + sessions.size() < maxSession) {
      sessionStore.addAll(sessions);
    }
  }

  @Override
  public void removeSession(Session<E> session) {
    try {
      session.stop();
    } catch (Exception e) {
      logger.error("sessioon stop error", e);
    }
    sessionStore.remove(session);
  }

  @Override
  public List<Session<E>> getSessions() {
    return sessionStore;
  }

  @Override
  public Session<E> getSession() {
    if (sessionStore.size() == 0) {
      return null;
    }

    if (sessionStore.size() == 1) {
      return sessionStore.get(0);
    }
    // FIXME
    return getSession(sessionStore);
  }

  @Override
  public Session<E> getSession(int index) {
    int idx = index % sessionStore.size();
    return sessionStore.get(idx);
  }

  /**
   * 获取会话 @see Session
   * 
   * @param sessions 会话列表 @see Session
   * @return 会话
   */
  private Session<E> getSession(List<Session<E>> sessions) {
    sessionIdx.set(sessionIdx.get() + 1);
    int idx = sessionIdx.get();
    if (idx >= sessions.size()) {
      idx = 0;
      sessionIdx.set(idx);
    }

    return sessions.get(idx);
  }

  @Override
  public void setMaxSession(int maxSession) {
    this.maxSession = maxSession;
  }

  @Override
  public int getMaxSession() {
    return maxSession;
  }

  @Override
  public boolean isFull() {
    return sessionStore.size() >= maxSession;
  }

  @Override
  public int size() {
    return sessionStore.size();
  }

  @Override
  public void stop() throws Exception {
    for (Session<E> session : sessionStore) {
      session.stop();
      logger.warn("stop session:[{}]", session.getConnectedInfo());
    }
    sessionStore.clear();
  }

}
