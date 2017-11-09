package com.sm.common.xaio.rpc;

/**
 * RPC动态代理创建
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午4:56:17
 */
public class NotifyAllProxyCreator extends ProxyCreatorSupport {

  @Override
  public <T> T createProxy() throws Exception {
    return createInvokerProxy(new RpcNotifyAllInvoker(peer), serviceInterfaces);
  }

}
