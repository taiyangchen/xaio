/**
 * 
 */
package com.sm.common.xaio.register;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.sm.common.libs.able.Stopable;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.libs.core.SimpleThreadFactory;
import com.sm.common.xaio.bo.ServerCategory;
import com.sm.common.xaio.bo.ServerInfo;
import com.sm.common.xaio.zk.XzkClient;
import com.sm.common.xaio.zk.ZkNodeConstants;

/**
 * ZkRegister
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 下午3:02:47
 */
public class ZkRegister extends LoggerSupport implements Register, ZkNodeConstants, Stopable {

  private XzkClient xzkClient;

  private ScheduledExecutorService executor = Executors
      .newSingleThreadScheduledExecutor(new SimpleThreadFactory(this.getClass().getSimpleName() + "-scheduled"));

  private int checkInterval = 10000;

  @Override
  public void registerServer(final ServerInfo serverInfo) {
    executor.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        String path = getPath(serverInfo);
        xzkClient.createPersistent(path);

        String ipPort = serverInfo.getPeerInfo().getName();
        if (xzkClient.createEphemeral(path + "/" + ipPort)) {
          logger.info("register server=[{}] to [{}] ", serverInfo.getPeerInfo(), serverInfo.getCategory());
        }
      }
    }, 1000, checkInterval, TimeUnit.MILLISECONDS);
  }

  private String getPath(ServerInfo serverInfo) {
    ServerCategory category = serverInfo.getCategory();
    String version = serverInfo.getVersion();
    if (StringUtils.isEmpty(version)) {
      version = DEFAULT_VERSION;
    }

    String path = ROOT + "/" + category.getDomain() + "/" + category.getGroup() + "/" + version;
    return path;
  }

  @Override
  public void stop() throws Exception {
    executor.shutdownNow();
  }

  public void setXzkClient(XzkClient xzkClient) {
    this.xzkClient = xzkClient;
  }


  public void setCheckInterval(int checkInterval) {
    this.checkInterval = checkInterval;
  }
}
