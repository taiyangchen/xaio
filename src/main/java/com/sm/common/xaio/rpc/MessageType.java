/**
 * 
 */
package com.sm.common.xaio.rpc;

/**
 * 消息类型
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午8:13:40
 */
public interface MessageType {

  /**
   * 获取消息类型
   * 
   * @return 消息类型
   */
  Type getType();

  enum Type {
    /**
     * 请求
     */
    REQUEST,
    /**
     * 响应
     */
    RESPONSE,
    /**
     * 通知
     */
    NOTIFY;
  }

}
