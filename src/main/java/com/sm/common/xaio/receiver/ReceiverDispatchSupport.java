package com.sm.common.xaio.receiver;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sm.common.libs.able.Computable;
import com.sm.common.libs.core.ConcurrentCache;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.core.MethodCache;
import com.sm.common.libs.core.SimpleThreadFactory;
//import com.sm.common.xaio.annotation.PeerCourse;

/**
 * 消息接收器分发支持
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午1:51:13
 */
public abstract class ReceiverDispatchSupport extends LoggerSupport {

  /**
   * 对等实体路由表
   */
  protected Map<String, Object> courseTable = new HashMap<>();

  /**
   * 方法路由表
   */
  protected Computable<String, Method> peerMethodRouter = ConcurrentCache.createComputable();

  protected int threads = Runtime.getRuntime().availableProcessors();

  /**
   * 消息接收的执行器
   */
  protected ExecutorService receiverExecutor = Executors.newFixedThreadPool(threads,
      new SimpleThreadFactory(this.getClass().getSimpleName() + "-" + threads + "-fixed"));

  /**
   * TPS监控器 FIXME
   */
  // protected TpsMonitor tpsMonitor = new TpsMonitor();

  /**
   * 方法缓存
   */
  protected final MethodCache methodCache = MethodCache.getInstance();

  public void setThreads(int threads) {
    if (receiverExecutor != null) {
      receiverExecutor.shutdownNow();
    }

    this.receiverExecutor = Executors.newFixedThreadPool(threads,
        new SimpleThreadFactory(this.getClass().getSimpleName() + "-" + threads + "fixed"));
  }

  // public void setTpsMonitor(TpsMonitor tpsMonitor) {
  // this.tpsMonitor = tpsMonitor;
  // }

}
