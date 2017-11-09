package com.sm.common.xaio.route;

/**
 * 总是选择第一个服务地址
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月1日 下午3:03:03
 */
public class ChooseFirst extends SchedulingSupport {

  @Override
  public void setTotal(int total) {
    this.total = total;
  }

  @Override
  public int next() {
    return 0;
  }

  @Override
  public SchedulingStrategy getStrategy() {
    return SchedulingStrategy.CHOOSE_FIRST;
  }

}
