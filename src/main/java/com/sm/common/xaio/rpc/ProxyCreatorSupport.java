/**
 * 
 */
package com.sm.common.xaio.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sm.common.libs.dynproxy.impl.ASMCreator;
import com.sm.common.libs.util.CollectionUtil;
import com.sm.common.libs.util.PackageUtil;
import com.sm.common.xaio.PeerSender;

/**
 * ProxyCreatorSupport
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月30日 下午5:36:46
 */
public abstract class ProxyCreatorSupport extends ASMCreator implements RpcProxy {

  /**
   * 代理接口
   */
  protected Class<?>[] serviceInterfaces;

  /**
   * 客户端
   */
  protected PeerSender peer;

  public void setPackages(List<String> packages) {
    List<Class<?>> list = new ArrayList<>();
    for (String pkg : packages) {
      List<Class<?>> subList = getInterfaces(pkg);
      if (CollectionUtil.isEmpty(subList)) {
        list.addAll(subList);
      }
    }

    serviceInterfaces = list.toArray(new Class<?>[0]);
  }

  private List<Class<?>> getInterfaces(String pkg) {
    List<Class<?>> list = new ArrayList<>();
    try {
      List<String> classNames = PackageUtil.getClassesInPackage(pkg);
      for (String className : classNames) {
        Class<?> clazz = Class.forName(className);
        if (clazz.isInterface()) {
          list.add(clazz);
        }
      }
      return list;
    } catch (IOException | ClassNotFoundException e) {
      logger.error("getClassesInPackage error", e);
      return null;
    }
  }

  @Override
  public Class<?>[] getServiceInterfaces() {
    return serviceInterfaces;
  }

  public void setServiceInterfaces(Class<?>[] serviceInterfaces) {
    this.serviceInterfaces = serviceInterfaces;
  }

  public void setPeer(PeerSender peer) {
    this.peer = peer;
  }

}
