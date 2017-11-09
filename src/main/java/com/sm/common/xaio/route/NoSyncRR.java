package com.sm.common.xaio.route;

/**
 * 不进行同步化的RoundRobin
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月1日 下午3:03:48
 */
public class NoSyncRR extends SchedulingSupport {

  /**
   * 当前索引号
   */
  private int index;

  @Override
  public void setTotal(int total) {
    this.total = total;
    index = 0;
  }

  @Override
  public int next() {
    int next = index++;
    // 溢出处理
    if (next < 0) {
      next = 0;
      index = next;
    }
    return next % total;
  }

  @Override
  public SchedulingStrategy getStrategy() {
    return SchedulingStrategy.NO_SYNC_RR;
  }

}
