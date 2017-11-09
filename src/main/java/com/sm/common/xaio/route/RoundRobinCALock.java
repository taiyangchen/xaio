package com.sm.common.xaio.route;

import java.util.concurrent.locks.Lock;

import com.sm.common.libs.concurrent.CompositeFastPathLock;

/**
 * 使用Composite Abortable Lock的轮询策略，提升并发性能
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月1日 下午3:07:02
 */
public class RoundRobinCALock extends SchedulingSupport {

  /**
   * 当前索引号
   */
  private int index;

  /**
   * Composite Abortable Lock
   */
  private final Lock lock = new CompositeFastPathLock();

  @Override
  public void setTotal(int total) {
    this.total = total;
    index = 0;
  }

  @Override
  public int next() {
    lock.lock();
    try {
      int next = index++;
      // 溢出处理
      if (next < 0) {
        next = 0;
        index = next;
      }
      return next % total;
    } finally {
      lock.unlock();
    }

  }

  @Override
  public SchedulingStrategy getStrategy() {
    return SchedulingStrategy.COMPOSITE_ABORTABLE_RR;
  }

}
