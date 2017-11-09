/**
 * 
 */
package com.sm.common.xaio.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.sm.common.libs.exception.MessageRuntimeException;
import com.sm.common.xaio.bo.ServerGroup;
import com.sm.common.xaio.zk.ZkNodeConstants;

/**
 * PathUtil
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月28日 下午5:20:43
 */
public abstract class PathUtil implements ZkNodeConstants {

  public static List<String> getLevelPaths(String path) {
    if (StringUtils.isBlank(path)) {
      return null;
    }

    String[] array = StringUtils.split(path, "/");
    if (ArrayUtils.isEmpty(array)) {
      return null;
    }

    List<String> list = new ArrayList<>(array.length);
    String ret = "";
    for (String str : array) {
      ret = ret + "/" + str;
      list.add(ret);
    }

    return list;
  }

  public static ServerGroup pathToGroup(String path) {
    String[] arrays = StringUtils.split(path, "/");
    if (arrays.length != 4) {
      throw new MessageRuntimeException("array length must 4,but current array length is {}", arrays.length);
    }
    if (!arrays[0].equals("xaio")) {
      throw new MessageRuntimeException("is not xaio path,which begin with {}", arrays[0]);
    }

    ServerGroup group = new ServerGroup();
    group.setVersion(arrays[3]);
    group.getCategory().setGroup(arrays[2]);
    group.getCategory().setDomain(arrays[1]);
    return group;
  }

}
