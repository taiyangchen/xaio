/**
 * 
 */
package com.sm.common.xaio;

import java.util.concurrent.TimeUnit;

/**
 * TransportConstant
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 上午11:08:39
 */
public interface TransportConstant {

  /**
   * 默认重试上限
   */
  int MAX_RETRY = 20;

  /**
   * 默认超时重试时间
   */
  long RETRY_TIMEOUT = 30 * 1000; // 30s

  /**
   * 默认服务IP
   */
  String ACCEPT_IP = "0.0.0.0";

  /**
   * 默认服务端口
   */
  int ACCEPT_PORT = 8888;

  /**
   * 默认最大客户端连接值
   */
  int MAX_CLIENT_CONNECTIONS = 2000;

  /**
   * 默认闲置时间，5分钟
   */
  int IDLE_TIME = 5 * 60;

  /**
   * socket数据接收缓冲区
   */
  int RECEIVE_BUFFER_SIZE = 1024;

  /**
   * 未完成三次握手队列 + 已经完成三次握手队列
   */
  int SO_BACKLOG_SIZE = 1024;

  /**
   * 默认待请求数
   */
  int PENDINGS_SIZE = 1024;

  /**
   * 默认的park时间
   */
  long PARK_TIME = TimeUnit.NANOSECONDS.convert(10, TimeUnit.SECONDS);
}
