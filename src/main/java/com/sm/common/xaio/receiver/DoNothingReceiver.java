package com.sm.common.xaio.receiver;

import com.sm.common.libs.able.Receiver;

/**
 * 不干任何事的消息接收器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 上午11:54:35
 */
public class DoNothingReceiver implements Receiver<Object> {

  @Override
  public void messageReceived(Object msg) {

  }

}
