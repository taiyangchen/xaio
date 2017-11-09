package com.sm.common.xaio.route;

/**
 * 路由调度算法
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月1日 下午3:04:51
 */
public interface Scheduling {

  /**
   * 获取路由总数
   * 
   * @return 总数
   */
  int getTotal();

  /**
   * 设置路由总数
   * 
   * @param total 总数
   */
  void setTotal(int total);

  /**
   * 返回下一请求的路由索引
   * 
   * @return 路由索引
   */
  int next();

  /**
   * 调度策略枚举，将<code>SchedulingStrategy</code>绑到调度接口上
   * 
   * @return 调度策略枚举@see SchedulingStrategy
   */
  SchedulingStrategy getStrategy();
}
