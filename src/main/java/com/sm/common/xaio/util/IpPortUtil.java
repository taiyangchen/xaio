/**
 * 
 */
package com.sm.common.xaio.util;

import com.sm.common.libs.exception.MessageRuntimeException;
import com.sm.common.xaio.bo.PeerInfo;

/**
 * IpPortUtil
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月29日 上午1:14:00
 */
public abstract class IpPortUtil {

  public static PeerInfo changeToPeer(String ipPort) {
    String[] server = ipPort.split(":");
    if (server.length != 2) {
      throw new MessageRuntimeException("host [{}] not match IP:PORT", ipPort);
    }

    PeerInfo peer = new PeerInfo();
    peer.setHostName(server[0]);
    peer.setPort(Integer.valueOf(server[1]));
    return peer;
  }

}
