/**
 * 
 */
package com.sm.common.xaio.subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.commons.lang3.StringUtils;

import com.sm.common.libs.bus.BusRegistry;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.util.CollectionUtil;
import com.sm.common.xaio.bo.PeerInfo;
import com.sm.common.xaio.bo.ServerCategory;
import com.sm.common.xaio.bo.ServerGroup;
import com.sm.common.xaio.util.IpPortUtil;
import com.sm.common.xaio.util.PathUtil;
import com.sm.common.xaio.zk.XzkClient;
import com.sm.common.xaio.zk.ZkNodeConstants;

/**
 * ZkSubscribe , FIXME 搞成标准的订阅模式
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 上午2:37:32
 */
public class ZkSubscribe extends LoggerSupport implements Subscribe, ZkNodeConstants {

  private XzkClient xzkClient;

  private BusRegistry<ServerGroup> busRegistry;

  private Map<String, List<SubscribeListener>> listenerMap = new HashMap<>();

  @Override
  public List<PeerInfo> subscribe(ServerCategory category) {
    return subscribe(category, DEFAULT_VERSION);
  }

  @Override
  public List<PeerInfo> subscribe(ServerCategory category, String version) {
    String path = getPath(category, version);
    addListener(path);

    List<String> ipPorts = xzkClient.getChildren(path);
    logger.info("subscribe [{}] ,server={}", category, ipPorts);
    List<PeerInfo> list = new ArrayList<>(ipPorts.size());
    for (String ipPort : ipPorts) {
      PeerInfo peer = IpPortUtil.changeToPeer(ipPort);
      list.add(peer);
    }

    return list;
  }

  @Override
  public void addListener(ServerCategory category, SubscribeListener listener) {
    addListener(category, DEFAULT_VERSION, listener);
  }

  @Override
  public void addListener(ServerCategory category, String version, SubscribeListener listener) {
    // FIXME to check
    String key = category.getGroup() + "@" + category.getDomain() + ":" + version;
    List<SubscribeListener> list = listenerMap.get(key);
    if (list == null) {
      list = new ArrayList<>();
      listenerMap.put(key, list);
    }

    list.add(listener);
  }

  private void addListener(String path) {
    xzkClient.addChildChangesListener(path, new IZkChildListener() {
      @Override
      public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        ServerGroup serverGroup = PathUtil.pathToGroup(parentPath);
        for (String ipPort : currentChilds) {
          PeerInfo peer = IpPortUtil.changeToPeer(ipPort);
          serverGroup.add(peer);
        }
        busRegistry.getSignalManager().signal(serverGroup, true);
        logger.warn("peer changed,current=[{}]", serverGroup);
        // add listener
        fireListeners(serverGroup);
      }
    });
  }

  private void fireListeners(ServerGroup serverGroup) {
    String key = getKey(serverGroup);
    List<SubscribeListener> list = listenerMap.get(key);
    if (CollectionUtil.isEmpty(list)) {
      return;
    }

    for (SubscribeListener listener : list) {
      listener.onChanged(serverGroup);
    }
  }

  private String getKey(ServerGroup serverGroup) {
    ServerCategory category = serverGroup.getCategory();
    String key = category.getGroup() + "@" + category.getDomain();
    String version = StringUtils.isBlank(serverGroup.getVersion()) ? DEFAULT_VERSION : serverGroup.getVersion();

    key = ":" + version;
    return key;
  }

  private String getPath(ServerCategory category, String version) {
    if (StringUtils.isBlank(version)) {
      version = DEFAULT_VERSION;
    }

    String path = ROOT + "/" + category.getDomain() + "/" + category.getGroup() + "/" + version;
    return path;
  }

  public void setXzkClient(XzkClient xzkClient) {
    this.xzkClient = xzkClient;
  }

  public void setBusRegistry(BusRegistry<ServerGroup> busRegistry) {
    this.busRegistry = busRegistry;
  }

}
