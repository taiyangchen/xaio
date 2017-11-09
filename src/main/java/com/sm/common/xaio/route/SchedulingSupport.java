package com.sm.common.xaio.route;

/**
 * 路由调度的简单支持
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月1日 下午3:05:16
 */
public abstract class SchedulingSupport implements Scheduling {

  /**
   * 路由总数
   */
  protected int total = 1;

  @Override
  public int getTotal() {
    return total;
  }

}
