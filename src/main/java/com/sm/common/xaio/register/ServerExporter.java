/**
 * 
 */
package com.sm.common.xaio.register;

import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.Server;
import com.sm.common.xaio.bo.ServerCategory;
import com.sm.common.xaio.bo.ServerInfo;

/**
 * ServerExporter
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 下午7:42:59
 */
public class ServerExporter extends LoggerSupport {

  private Server server;

  private Register register;

  private String category;

  private String version;

  public void export() {
    ServerInfo serverInfo = createServerInfo();
    register.registerServer(serverInfo);
  }

  private ServerInfo createServerInfo() {
    ServerInfo serverInfo = new ServerInfo();
    serverInfo.setPeerInfo(server.getPeer());
    ServerCategory serverCategory = new ServerCategory();
    serverCategory.setCategory(category);
    serverInfo.setCategory(serverCategory);
    serverInfo.setVersion(version);

    return serverInfo;
  }

  public void setServer(Server server) {
    this.server = server;
  }

  public void setRegister(Register register) {
    this.register = register;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setVersion(String version) {
    this.version = version;
  }

}
