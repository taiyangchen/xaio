/**
 * 
 */
package com.sm.common.xaio.messagepack;

/**
 * UserInfo
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月9日 下午5:58:43
 */
//@Message
public class UserInfo extends MessagePackObject{


  private String userName;
  private int userID;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public UserInfo buildUserName(String userName) {
    this.userName = userName;
    return this;
  }

  public UserInfo buildUserID(int userID) {
    this.userID = userID;
    return this;
  }

  @Override
  public String toString() {
    return "UserInfo [userName=" + userName + ", userID=" + userID + "]";
  }
}
