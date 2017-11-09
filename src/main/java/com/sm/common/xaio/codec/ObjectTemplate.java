package com.sm.common.xaio.codec;

import java.io.IOException;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.unpacker.Unpacker;

/**
 * 添加Object对象的模版
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月26日 下午6:55:57
 * @param <T>
 */
public class ObjectTemplate<T> extends AbstractTemplate<T> {

  /**
   * 模版注册器
   */
  private TemplateRegistry registry;

  public ObjectTemplate(TemplateRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void write(Packer pk, T target, boolean required) throws IOException {
    if (target == null) {
      if (required) {
        throw new MessageTypeException("Attempted to write null");
      }
      pk.writeNil();
      return;
    }

    pk.writeArrayBegin(2);
    String className = target.getClass().getName();
    pk.write(className);
    pk.write(target);
    pk.writeArrayEnd();
  }

  @Override
  public T read(Unpacker u, T to, boolean required) throws IOException, MessageTypeException {
    if (!required && u.trySkipNil()) {
      return null;
    }
    u.readArrayBegin();
    String className = u.readString();

    try {
      @SuppressWarnings("unchecked")
      Class<T> clazz = (Class<T>) Class.forName(className);
      @SuppressWarnings("unchecked")
      Template<T> template = registry.lookup(clazz);
      T result = template.read(u, to, required);
      return result;
    } catch (ClassNotFoundException e) {
      // ignore
      return null;
    } finally {
      u.readArrayEnd();
    }

  }

}
