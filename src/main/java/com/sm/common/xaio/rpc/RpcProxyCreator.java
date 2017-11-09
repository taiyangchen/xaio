package com.sm.common.xaio.rpc;

/**
 * RPC动态代理创建
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午4:56:17
 */
public class RpcProxyCreator extends ProxyCreatorSupport {

  /**
   * 发送等待时间，毫秒
   */
  private long duration = 10000; // ms

  @Override
  public <T> T createProxy() throws Exception {
    return createInvokerProxy(new RpcInvoker(peer, duration), serviceInterfaces);
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

}
