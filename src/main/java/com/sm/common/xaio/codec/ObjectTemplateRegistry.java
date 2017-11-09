package com.sm.common.xaio.codec;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msgpack.template.CollectionTemplate;
import org.msgpack.template.GenericCollectionTemplate;
import org.msgpack.template.GenericMapTemplate;
import org.msgpack.template.ListTemplate;
import org.msgpack.template.MapTemplate;
import org.msgpack.template.SetTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;

/**
 * 适应于任何对象类型的模板注册器
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2017年6月26日 下午6:56:10
 */
public class ObjectTemplateRegistry extends TemplateRegistry {

  /**
   * 对象模版
   */
  private Template<Object> objectTemplate;

  public ObjectTemplateRegistry(TemplateRegistry registry) {
    super(registry);
    objectTemplate = new ObjectTemplate<Object>(this);
    this.register(Object.class, objectTemplate);
    registerCollection();
  }

  @Override
  public Template<?> lookup(Type targetType) {
    if (targetType == null) {
      return null;
    }
    if (targetType instanceof TypeVariable) {
      return objectTemplate;
    }

    return super.lookup(targetType);
  }

  /**
   * 注册集合模版
   */
  protected void registerCollection() {
    register(List.class, new ListTemplate<Object>(objectTemplate));
    register(Set.class, new SetTemplate<Object>(objectTemplate));
    register(Collection.class, new CollectionTemplate<Object>(objectTemplate));
    register(Map.class, new MapTemplate<Object, Object>(objectTemplate, objectTemplate));
    registerGeneric(List.class, new GenericCollectionTemplate(this, ListTemplate.class));
    registerGeneric(Set.class, new GenericCollectionTemplate(this, SetTemplate.class));
    registerGeneric(Collection.class, new GenericCollectionTemplate(this, CollectionTemplate.class));
    registerGeneric(Map.class, new GenericMapTemplate(this, MapTemplate.class));
  }

}
