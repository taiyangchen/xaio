/**
 * 
 */
package com.sm.common.xaio;

import com.sm.common.libs.exception.MessageException;
import com.sm.common.libs.util.MessageUtil;

/**
 * 传输层的异常
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年12月22日 下午7:10:37
 */
public class TransportException extends MessageException {

  /**
   * 
   */
  private static final long serialVersionUID = -8138373672665592166L;

  /**
   * 构造一个空的异常.
   */
  public TransportException() {
    super();
  }

  /**
   * 构造一个异常, 指明异常的详细信息.
   * 
   * @param message 详细信息
   */
  public TransportException(String message) {
    super(message);
  }

  /**
   * 构造一个异常, 指明引起这个异常的起因.
   * 
   * @param cause 异常的起因
   */
  public TransportException(Throwable cause) {
    super(cause);
  }

  /**
   * 构造一个异常, 指明引起这个异常的起因.
   * 
   * @param message 详细信息
   * @param cause 异常的起因
   */
  public TransportException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * 构造一个异常, 参数化详细信息
   * 
   * @param message 详细信息
   * @param params 参数表
   */
  public TransportException(String message, Object... params) {
    super(MessageUtil.formatLogMessage(message, params));
  }

  /**
   * 构造一个异常, 参数化详细信息,指明引起这个异常的起因
   * 
   * @param message 详细信息
   * @param cause 异常的起因
   * @param params 参数表
   */
  public TransportException(String message, Throwable cause, Object... params) {
    super(MessageUtil.formatLogMessage(message, params), cause);
  }

}
