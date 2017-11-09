package com.sm.common.xaio.route;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import com.sm.common.libs.concurrent.CompositeFastPathLock;

/**
 * 根据服务权重数轮询路由，使用Composite Abortable Lock，提升并发性能
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月1日 下午3:07:10
 */
public class RoundWeightCALock extends SchedulingSupport {

  /**
   * 当前索引号
   */
  private int index;

  /**
   * 服务索引到权重的映射表
   */
  private Map<Integer, Integer> weightsMap;

  /**
   * Composite Abortable Lock
   */
  private final Lock lock = new CompositeFastPathLock();

  @Override
  public int next() {
    lock.lock();
    try {
      int next = index++;
      // 溢出处理
      if (next < 0) {
        next = 0;
        index = 0;
      }
      return weightsMap.get(next % total);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int getTotal() {
    return weightsMap.size();
  }

  /**
   * 通过服务权重数初始化 {@link #total} 和 {@link #weightsMap}
   * 
   * @param weights 权重数组
   */
  public void setWeights(int[] weights) {
    int length = weights.length;

    weightsMap = new HashMap<>(length);
    int index = 0;

    for (int i = 0; i < length; i++) {
      int weight = weights[i];
      total += weight;
      int max = index + weight;
      for (; index < max; index++) {
        weightsMap.put(index, i);
      }
    }
  }

  @Override
  public void setTotal(int total) {
    // ignore
  }

  @Override
  public SchedulingStrategy getStrategy() {
    return SchedulingStrategy.COMPOSITE_ABORTABLE_WEIGHT;
  }

}
