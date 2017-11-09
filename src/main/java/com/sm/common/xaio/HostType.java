package com.sm.common.xaio;

import com.sm.common.xaio.bo.ServerInfo;

/**
 * Host Type
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 上午11:10:33
 */
public enum HostType implements TransportConstant {

  IP {
    @Override
    public ServerInfo parse(String[] servers) {
      return new ServerInfo(servers[0].trim(), ACCEPT_PORT);
    }

    @Override
    public int serverLength() {
      return 1;
    }
  },
  IP_PORT {
    @Override
    public ServerInfo parse(String[] servers) {
      return new ServerInfo(servers[0].trim(), Integer.parseInt(servers[1].trim()));
    }

    @Override
    public int serverLength() {
      return 2;
    }
  },
  IP_PORT_WEIGHT {
    @Override
    public ServerInfo parse(String[] servers) {
      return new ServerInfo(servers[0].trim(), Integer.parseInt(servers[1].trim()),
          Integer.parseInt(servers[2].trim()));
    }

    @Override
    public int serverLength() {
      return 3;
    }
  };

  public abstract ServerInfo parse(String[] servers);

  public abstract int serverLength();

  public static ServerInfo toServerInfo(String[] servers) {
    if (servers == null) {
      return null;
    }

    for (HostType type : HostType.values()) {
      if (type.serverLength() == servers.length) {
        return type.parse(servers);
      }
    }

    return null;
  }
}
