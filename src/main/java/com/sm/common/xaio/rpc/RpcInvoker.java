package com.sm.common.xaio.rpc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;

import com.sm.common.libs.able.Computable;
import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.libs.core.ConcurrentCache;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.dynproxy.ObjectInvoker;
import com.sm.common.libs.util.MethodUtil;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.PeerSender;
import com.sm.common.xaio.annotation.Callback;
import com.sm.common.xaio.annotation.NotifyAll;
import com.sm.common.xaio.cluster.PeerSendCluster;
import com.sm.common.xaio.codec.NullObject;
import com.sm.common.xaio.util.SessionUtil;

/**
 * PRC代理调用器 @see ObjectInvoker
 * <p>
 * add callback FIXME
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午4:56:34
 */
public class RpcInvoker extends LoggerSupport implements ObjectInvoker {

  private static final long serialVersionUID = -41656664130938749L;

  /**
   * 对等实体
   */
  private PeerSender peer;

  /**
   * 发送时间
   */
  private long duration;

  /**
   * 回调方法缓存
   */
  private Computable<Method, Callback> cache = ConcurrentCache.createComputable();

  // FIXME
  public <E> RpcInvoker(PeerSender peer, long duration) throws Exception {
    this.peer = peer;
    this.duration = duration;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object... arguments) throws Throwable {
    MethodDesc methodDesc = new MethodDesc(method.getName(), arguments);
    // FIXME
    if (MethodUtil.isReturnVoid(method)) {
      handlerNotify(method, arguments);
      return null;
    }
    Message<?, ?> response = peer.sendAndWait(new RpcRequest(methodDesc), duration, TimeUnit.MILLISECONDS);
    if (response == null) {
      return null;
    }

    Object result = response.getBean();
    // FIXME need?
    SessionUtil.unbind(response);
    // send on callback to FIXME
    if (result instanceof NullObject) {
      return null;
    }
    
    if (!(result instanceof Exception)) {
      return result;
    }

    logger.error("invoker method={},arguments={} error={}", method.getName(), Arrays.toString(arguments), result);
    // FIXME
    return null;
  }

  /**
   * 查找异步消息回调
   * 
   * @param method 方法名
   * @return 异步消息回调 @see Callback
   */
  private Callback findCallback(final Method method) {
    return cache.get(method, new Callable<Callback>() {
      @Override
      public Callback call() throws Exception {
        Callback ret = method.getAnnotation(Callback.class);
        if (ret != null) {
          return ret;
        }
        ret = method.getDeclaringClass().getAnnotation(Callback.class);
        return ret;
      }
    });
  }

  private void handlerNotifyAll(Method method, Object... arguments) {
    RpcNotify notify = new RpcNotify(new MethodDesc(method.getName(), arguments));
    // FIXME
    if (!(peer instanceof PeerSendCluster)) {
      peer.send(notify);
      return;
    }

    PeerSendCluster<?> psc = (PeerSendCluster<?>) peer;
    for (int i = 0, size = psc.size(); i < size; i++) {
      psc.send(notify);
    }
  }

  /**
   * 通知类型请求处理
   * 
   * @param method 方法
   * @param arguments 方法参数
   */
  private void handlerNotify(Method method, Object... arguments) {
    NotifyAll notifyAll = method.getAnnotation(NotifyAll.class);
    if (notifyAll != null) {
      handlerNotifyAll(method, arguments);
      return;
    }

    Callback callback = findCallback(method);
    if (callback == null) {
      peer.send(new RpcNotify(new MethodDesc(method.getName(), arguments)));
      return;
    }

    int index = callback.index();
    if (index < 0 || index >= arguments.length) {
      logger.warn("index error,ignore");
      peer.send(new RpcNotify(new MethodDesc(method.getName(), arguments)));
      return;
    }

    notifyOnCallback(method, index, arguments);
  }

  private void notifyOnCallback(Method method, int index, Object... arguments) {
    Object callbackObject = arguments[index];
    Object[] args = ArrayUtils.remove(arguments, index);

    RpcNotify notify = new RpcNotify(new MethodDesc(method.getName(), args));
    notify.setCallback(true);

    if (callbackObject instanceof ResponseCallback) {
      peer.send((ResponseCallback<?>) callbackObject, notify);
      // FIXME
      return;
    }
    // FIXME
    logger.warn("no callback class found,ignore");
    peer.send(notify);
  }

}
