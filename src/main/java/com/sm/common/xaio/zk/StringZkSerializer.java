/**
 * 
 */
package com.sm.common.xaio.zk;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

/**
 * 用于<code>String</code>类型的Serializer
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月28日 下午3:37:30
 */
public class StringZkSerializer implements ZkSerializer {

  @Override
  public byte[] serialize(Object data) throws ZkMarshallingError {
    return data.toString().getBytes();
  }

  @Override
  public Object deserialize(byte[] bytes) throws ZkMarshallingError {
    return new String(bytes);
  }

}
