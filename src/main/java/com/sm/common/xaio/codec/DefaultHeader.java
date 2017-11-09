/**
 * 
 */
package com.sm.common.xaio.codec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.sm.common.libs.codec.MessageHeader;
import com.sm.common.libs.codec.ProtocolException;
import com.sm.common.libs.core.ToStringSupport;

/**
 * Protobuf内部定义头结构
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月11日 下午2:55:32
 */
public class DefaultHeader extends ToStringSupport implements MessageHeader {

  public static final int FIXED_HEADER_SIZE = 12;

  private static final String magicField = "rpc-head";

  private byte[] ident = magicField.getBytes();

  private short version;

  private short compress;

  private int bodySize;

  private short checksum;

  private short reserve;

  private int id;

  private int type;

  @Override
  public byte[] toArray() {
    ByteBuffer buf = ByteBuffer.allocate(FIXED_HEADER_SIZE);
    buf.order(ByteOrder.LITTLE_ENDIAN);

    buf.putInt(bodySize);
    buf.putInt(id);
    buf.putInt(type);

    return buf.array();
  }

  @Override
  public void parseForm(byte[] array) throws ProtocolException {
    ByteBuffer buf = ByteBuffer.wrap(array);
    buf.order(ByteOrder.LITTLE_ENDIAN);

    this.bodySize = buf.getInt();
    this.id = buf.getInt();
    this.type = buf.getInt();

  }

  @Override
  public int getHeaderSize() {
    return FIXED_HEADER_SIZE;
  }

  @Override
  public int getId() {
    return id;
  }

  public short getVersion() {
    return version;
  }

  public void setVersion(short version) {
    this.version = version;
  }

  public short getCompress() {
    return compress;
  }

  public void setCompress(short compress) {
    this.compress = compress;
  }

  @Override
  public int getBodySize() {
    return bodySize;
  }

  @Override
  public void setBodySize(int bodySize) {
    this.bodySize = bodySize;
  }

  public short getChecksum() {
    return checksum;
  }

  public void setChecksum(short checksum) {
    this.checksum = checksum;
  }

  public short getReserve() {
    return reserve;
  }

  public void setReserve(short reserve) {
    this.reserve = reserve;
  }

  public byte[] getIdent() {
    return ident;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public int getType() {
    return type;
  }

  @Override
  public void setType(int type) {
    this.type = type;
  }

  @Override
  public Class<?> getClassType() {
    return null;
  }

  @Override
  public void setClassType(Class<?> type) {

  }

}
