package com.sm.common.xaio.rpc;

/**
 * RPC动态代理简单工厂
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午2:22:48
 */
public abstract class RpcProxyFactory {

  /**
   * 创建代理对象
   * 
   * @param proxy RPC动态代理器 @see RpcProxy
   * @return 代理对象
   * @throws Exception
   */
  public static <T> T createProxy(RpcProxy proxy) throws Exception {
    return proxy.createProxy();
  }

}
