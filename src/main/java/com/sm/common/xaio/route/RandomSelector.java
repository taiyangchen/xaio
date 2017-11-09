
package com.sm.common.xaio.route;

import java.util.Random;

import com.sm.common.libs.util.RandomUtil;

/**
 * 随机选择路由
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月1日 下午3:04:01
 */
public class RandomSelector extends SchedulingSupport {

  /**
   * 随机生成器
   */
  private Random random = new Random(RandomUtil.next(1, Integer.MAX_VALUE));

  @Override
  public void setTotal(int total) {
    this.total = total;
  }

  @Override
  public int next() {
    int next = RandomUtil.next(random, 0, total - 1);

    return next;
  }

  @Override
  public SchedulingStrategy getStrategy() {
    return SchedulingStrategy.RANDOM;
  }

}
