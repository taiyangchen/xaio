package com.sm.common.xaio.route;

/**
 * 路由调度策略枚举
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月1日 下午3:05:03
 */
public enum SchedulingStrategy {
  /**
   * 总是选择第一个服务地址
   */
  CHOOSE_FIRST {
    @Override
    public Scheduling getScheduling() {
      return new ChooseFirst();
    }
  },
  /**
   * 不进行同步化的RoundRobin
   */
  NO_SYNC_RR {
    @Override
    public Scheduling getScheduling() {
      return new NoSyncRR();
    }
  },
  /**
   * 轮询
   */
  ROUND_ROBIN {
    @Override
    public Scheduling getScheduling() {
      return new RoundRobin();
    }
  },
  /**
   * 带权重的轮询
   */
  RR_WITH_WEIGHT {
    @Override
    public Scheduling getScheduling() {
      return new RoundWeight();
    }
  },
  /**
   * 哈希值路由
   */
  HASH {
    @Override
    public Scheduling getScheduling() {
      return new Hashing();
    }
  },
  /**
   * 随机
   */
  RANDOM {
    @Override
    public Scheduling getScheduling() {
      return new RandomSelector();
    }
  },
  /**
   * 使用Composite Abortable Lock的带权重轮询
   */
  COMPOSITE_ABORTABLE_WEIGHT {
    @Override
    public Scheduling getScheduling() {
      return new RoundWeightCALock();
    }
  },
  /**
   * 使用Composite Abortable Lock的轮询
   */
  COMPOSITE_ABORTABLE_RR {
    @Override
    public Scheduling getScheduling() {
      return new RoundRobinCALock();
    }
  };

  /**
   * 获取路由调度算法
   * 
   * @return 路由调度算法 @see Scheduling
   */
  public abstract Scheduling getScheduling();
}
