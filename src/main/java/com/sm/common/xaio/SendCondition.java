package com.sm.common.xaio;

import java.util.concurrent.TimeUnit;

import com.sm.common.libs.core.ToStringSupport;

/**
 * 发送条件
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月9日 下午12:06:00
 */
public class SendCondition extends ToStringSupport {

  /**
   * 发送对象
   */
  private Object bean;

  /**
   * 超时时间
   */
  private long duration = 10000;

  /**
   * 超时时间单位
   */
  private TimeUnit unit = TimeUnit.MILLISECONDS;

  public SendCondition() {

  }

  public SendCondition(Object bean) {
    this.bean = bean;
  }

  public SendCondition(Object bean, long duration, TimeUnit unit) {
    this.bean = bean;
    this.duration = duration;
    this.unit = unit;
  }

  public Object getBean() {
    return bean;
  }

  public void setBean(Object bean) {
    this.bean = bean;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public TimeUnit getUnit() {
    return unit;
  }

  public void setUnit(TimeUnit unit) {
    this.unit = unit;
  }

}
