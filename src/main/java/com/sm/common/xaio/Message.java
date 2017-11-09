/**
 * 
 */
package com.sm.common.xaio;

import java.util.concurrent.atomic.AtomicInteger;

import com.sm.common.libs.able.Signal;
import com.sm.common.libs.core.ToStringSupport;

/**
 * 网络传输通用信息
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月11日 上午2:32:07
 */
public class Message<K, V> extends ToStringSupport implements Signal {

  /**
   * 请求ID生成器
   */
  private static final AtomicInteger uid = new AtomicInteger();

  /**
   * 请求类型 @see MessageType
   */
  protected K type;

  /**
   * 消息体
   */
  protected V bean;

  /**
   * 请求ID
   */
  protected int id;

  public Message(K type, V bean) {
    this.type = type;
    this.bean = bean;
    this.id = uid.incrementAndGet();
  }

  public Message(K type, V bean, int id) {
    this.type = type;
    this.bean = bean;
    this.id = id;
  }

  public K getType() {
    return type;
  }

  public void setType(K type) {
    this.type = type;
  }

  public V getBean() {
    return bean;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Class<?> getClazz() {
    return bean.getClass();
  }

}
