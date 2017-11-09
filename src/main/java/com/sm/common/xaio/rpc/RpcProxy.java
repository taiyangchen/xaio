package com.sm.common.xaio.rpc;

/**
 * RPC动态代理器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午4:57:01
 */
public interface RpcProxy {

  /**
   * 创建动态代理
   * 
   * @return 动态代理
   * @throws Exception
   */
  <T> T createProxy() throws Exception;

  /**
   * 获取代理接口集
   * 
   * @return 代理接口集
   */
  Class<?>[] getServiceInterfaces();

}
