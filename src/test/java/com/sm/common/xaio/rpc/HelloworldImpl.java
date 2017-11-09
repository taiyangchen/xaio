package com.sm.common.xaio.rpc;

import com.sm.common.libs.able.ResponseCallback;
import com.sm.common.libs.core.LoggerSupport;
import com.sm.common.xaio.annotation.PeerCourse;
import com.sm.common.xaio.messagepack.UserInfo;

/**
 * HelloworldImpl
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2015-7-29 下午6:16:31
 */
public class HelloworldImpl extends LoggerSupport implements Helloworld, PeerCourse {

	@Override
	public String getName(String name) {
//	  return null;
    return "hello world : " + name;
  }

	@Override
	public void printMessage(String message) {
		logger.info("message-->{}", message);
	}

	@Override
	public void printMessage(String message, int num) {
		for (int i = 0; i < num; i++) {
			logger.info("message-->{}", message);
		}
	}

	// FIXME
	public String messageCallback(String message) {
		return "message-->" + message + "." + System.currentTimeMillis();
	}

	@Override
	public void messageCallback(final ResponseCallback<String> callback, String message) {
		try {
			callback.onSuccess(message);
		} catch (Exception e) {
			callback.onException(e);
		}
	}

  @Override
  public UserInfo echo(UserInfo user) {
    return user;
  }
  
  @Override
  public int timeout() {
    try {
      Thread.sleep(11*1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    return 0;
  }

}
