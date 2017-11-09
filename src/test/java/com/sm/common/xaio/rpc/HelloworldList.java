package com.sm.common.xaio.rpc;

import java.util.List;

import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.xaio.annotation.Callback;
import com.sm.common.xaio.messagepack.UserInfo;

/**
 * HelloworldList
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午5:14:26
 */
public interface HelloworldList {

  List<String> getName(String name);

  void printMessage(String message);

  void printMessage(String message, int num);
  
  List<UserInfo> echo(UserInfo user);

  @Callback
  void messageCallback(ResponseCallback<String> callback, String message);

  List<Integer> timeout();

}
