package com.sm.common.xaio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对等实体调用方法，标记在<code>Class</code>说明所有方法均接受对等实体请求
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午4:53:35
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PeerMethod {

  /**
   * 返回类型
   * 
   * @return 返回类型
   */
  Class<?> returnType() default Object.class;

  /**
   * 是否携带信号量
   * 
   * @return 是否携带信号量
   */
  boolean signal() default true;

}
