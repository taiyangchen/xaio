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
public class ProtoHeader extends ToStringSupport implements MessageHeader {

  public static final int PROTO_HEADER_SIZE = 20;

  private static final String magicField = "rpc-head";

  private byte[] ident = magicField.getBytes();

  private short version;

  private short compress;

  private int bodySize;

  private short checksum;

  private short reserve;

  @Override
  public byte[] toArray() {
    ByteBuffer buf = ByteBuffer.allocate(PROTO_HEADER_SIZE);
    buf.order(ByteOrder.LITTLE_ENDIAN);

    buf.put(ident);
    buf.putShort(this.version);
    buf.putShort(this.compress);
    buf.putInt(this.bodySize);
    buf.putShort(this.checksum);
    buf.putShort(this.reserve);

    return buf.array();
  }

  @Override
  public void parseForm(byte[] array) throws ProtocolException {
    ByteBuffer buf = ByteBuffer.wrap(array);
    buf.order(ByteOrder.LITTLE_ENDIAN);
    this.ident = new byte[magicField.getBytes().length];
    buf.get(ident);

    this.version = buf.getShort();
    this.compress = buf.getShort();
    this.bodySize = buf.getInt();
    this.checksum = buf.getShort();
    this.reserve = buf.getShort();

    String tmpIdent = new String(ident);
    if (!magicField.equals(tmpIdent)) {
      throw new ProtocolException("ident:{} not equals {}", tmpIdent, magicField);
    }

  }

  @Override
  public int getHeaderSize() {
    return PROTO_HEADER_SIZE;
  }

  @Override
  public int getId() {
    return 0;
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

  }

  @Override
  public int getType() {
    return 0;
  }

  @Override
  public void setType(int type) {

  }

  @Override
  public Class<?> getClassType() {
    return null;
  }

  @Override
  public void setClassType(Class<?> type) {

  }

}
