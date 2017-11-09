/**
 * 
 */
package com.sm.common.xaio.bo;

import java.util.Objects;

import com.sm.common.libs.core.ToStringSupport;
import com.sm.common.libs.util.CastUtil;

/**
 * 服务类别，由服务域及域下组确定唯一
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月27日 下午7:00:21
 */
public class ServerCategory extends ToStringSupport {

  /**
   * 服务域
   */
  private String domain;

  /**
   * 服务组
   */
  private String group;

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public void setCategory(String category) {
    int idx = category.indexOf('@');
    if (-1 != idx) {
      setDomain(category.substring(idx + 1));
      setGroup(category.substring(0, idx));
      return;
    }

    setDomain(category);
  }

  /**
   * 比较域是否相同
   * 
   * @param other 比较对象
   * @return 域是否相同
   */
  public boolean isSameDomain(ServerCategory other) {
    if (null == other) {
      return false;
    }

    if (domain == null) {
      return (null == other.domain);
    }

    return domain.equals(other.domain);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((domain == null) ? 0 : domain.hashCode());
    result = prime * result + ((group == null) ? 0 : group.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    // 到此处类型必相同
    ServerCategory other = CastUtil.cast(obj);
    return Objects.equals(domain, other.domain) && Objects.equals(group, other.group);
  }

}
