/**
 * 
 */
package com.sm.common.xaio.receiver;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.Session;
import com.sm.common.xaio.util.SessionUtil;

/**
 * just echo
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月21日 下午8:31:04
 */
public class EchoReceiver<E> extends LoggerSupport implements Receiver<E> {

  private boolean cycle;

  @Override
  public void messageReceived(E message) {
    // Message<Integer, Object> msg = CastUtil.cast(message);
    // logger.info("received--> [uid : {}, bean : {}]", msg.getId(),
    // msg.getBean());
    Session<E> session = SessionUtil.getSession(message);
    logger.info("{} receiver message : {}",session.getConnectedInfo(),message);

    if (canSend(session)) {
      // FIXME
      // msg.setType(1);
      session.send(message);
    }
  }

  private boolean canSend(Session<E> session) {
    if (session == null) {
      return false;
    }

    return cycle || session.getType() == Session.Type.SERVER;
  }

  public void setCycle(boolean cycle) {
    this.cycle = cycle;
  }

}
