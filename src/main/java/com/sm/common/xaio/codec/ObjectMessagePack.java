package com.sm.common.xaio.codec;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sm.common.libs.util.CollectionUtil;
import com.sm.common.libs.util.PackageUtil;
import com.sm.common.xaio.rpc.MethodDesc;

/**
 * 适应于任何对象类型的<code>MessagePack</code>编解码器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月26日 下午6:55:44
 */
public class ObjectMessagePack extends MessagePack {

  private static final Logger logger = LoggerFactory.getLogger(ObjectMessagePack.class);

  public ObjectMessagePack() {
    super(new ObjectTemplateRegistry(null));
    this.register(MethodDesc.class);
    this.register(NullObject.class);
  }

  void registerPackages(List<String> packages) {
    List<Class<?>> list = new ArrayList<>();
    for (String pkg : packages) {
      List<Class<?>> subList = getClasses(pkg);
      // FIXME
      Collections.reverse(subList);
      if (CollectionUtil.isNotEmpty(subList)) {
        list.addAll(subList);
      }
    }

    for (Class<?> clazz : list) {
      this.register(clazz);
    }
  }

  void registerPackage(String pkg) {
    List<Class<?>> list = getClasses(pkg);
    // FIXME
    Collections.reverse(list);
    for (Class<?> clazz : list) {
      this.register(clazz);
    }
  }

  private List<Class<?>> getClasses(String pkg) {
    List<Class<?>> list = new ArrayList<>();
    try {
      List<String> classNames = PackageUtil.getClassesInPackage(pkg);
      for (String className : classNames) {
        Class<?> clazz = Class.forName(className);
        int mod = clazz.getModifiers();
        // FIXME class
        if (Modifier.isPublic(mod) && !Modifier.isInterface(mod) && !Modifier.isAbstract(mod)) {
          list.add(clazz);
        }
      }
      return list;
    } catch (IOException | ClassNotFoundException e) {
      logger.error("getClassesInPackage error", e);
      return null;
    }
  }

}
