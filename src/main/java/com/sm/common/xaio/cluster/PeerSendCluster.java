/**
 * 
 */
package com.sm.common.xaio.cluster;

import com.sm.common.libs.able.Bootstrap;
import com.sm.common.xaio.PeerSender;

/**
 * PeerSendCluster
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月29日 下午4:57:39
 */
public interface PeerSendCluster<E> extends Cluster<E>, Bootstrap, PeerSender{

}
