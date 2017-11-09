package com.sm.common.xaio.rpc;

import java.lang.reflect.Method;

import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.dynproxy.ObjectInvoker;
import com.sm.common.xaio.PeerSender;
import com.sm.common.xaio.cluster.PeerSendCluster;

/**
 * PRC代理调用器 @see ObjectInvoker
 * <p>
 * add callback FIXME
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午4:56:34
 */
public class RpcNotifyAllInvoker extends LoggerSupport implements ObjectInvoker {

  private static final long serialVersionUID = -41656664130938749L;

  /**
   * 对等实体
   */
  private PeerSender peer;

  // FIXME
  public <E> RpcNotifyAllInvoker(PeerSender peer) throws Exception {
    this.peer = peer;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object... arguments) throws Throwable {
    RpcNotify notify = new RpcNotify(new MethodDesc(method.getName(), arguments));
    // FIXME
    if (!(peer instanceof PeerSendCluster)) {
      peer.send(notify);
      return null;
    }

    PeerSendCluster<?> psc = (PeerSendCluster<?>) peer;
    for (int i = 0, size = psc.size(); i < size; i++) {
      psc.send(notify);
    }

    return null;
  }

}
