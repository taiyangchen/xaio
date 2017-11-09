package com.sm.common.xaio.rpc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.dynproxy.ObjectInvoker;
import com.sm.common.libs.util.MethodUtil;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.PeerSender;
import com.sm.common.xaio.TransportException;
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
public class RpcInvokerAll extends LoggerSupport implements ObjectInvoker {

  private static final long serialVersionUID = -41656664130938749L;

  /**
   * 对等实体
   */
  private PeerSender peer;

  /**
   * 发送时间
   */
  private long duration;

  // FIXME
  public <E> RpcInvokerAll(PeerSender peer, long duration) throws Exception {
    this.peer = peer;
    this.duration = duration;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object... arguments) throws Throwable {
    List<Object> list = new ArrayList<>();
    MethodDesc methodDesc = new MethodDesc(method.getName(), arguments);
    // FIXME
    if (MethodUtil.isReturnVoid(method)) {
      return list;
    }
    RpcRequest request = new RpcRequest(methodDesc);
    // FIXME
    if (!(peer instanceof PeerSendCluster)) {
      Object result = sendAndWait(peer, request);
      if (result != null) {
        list.add(result);
      }
      return list;
    }

    PeerSendCluster<?> psc = (PeerSendCluster<?>) peer;
    for (int i = 0, size = psc.size(); i < size; i++) {
      Object result = sendAndWait(psc, request);
      if (result != null) {
        list.add(result);
      }
    }

    return list;
  }

  private Object sendAndWait(PeerSender peer, RpcRequest request) throws TransportException {
    Message<?, ?> response = peer.sendAndWait(request, duration, TimeUnit.MILLISECONDS);
    if (response == null) {
      return null;
    }

    Object result = response.getBean();
    SessionUtil.unbind(response);
    if (result instanceof NullObject) {
      return null;
    }
    if (!(result instanceof Exception)) {
      return result;
    }

    logger.error("invoker method={},arguments={} error={}", request.getMethodName(),
        Arrays.toString(request.getParams()), result);
    return null;
  }


}
