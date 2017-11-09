package com.sm.common.xaio.receiver;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ClassUtils;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.util.ClassUtil;
import com.sm.common.libs.util.MethodUtil;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.PeerSender;
import com.sm.common.xaio.codec.NullObject;
import com.sm.common.xaio.rpc.MethodDesc;
import com.sm.common.xaio.rpc.RpcNotify;
import com.sm.common.xaio.rpc.RpcResponse;
import com.sm.common.xaio.util.SessionUtil;

/**
 * 请求接收分发处理
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午2:34:06
 */
public class RpcStyleDispatcher extends ReceiverDispatchSupport implements Receiver<Message<Integer, MethodDesc>> {

  /**
   * 获取对等实体的调用方法
   * 
   * @param methodNamemethodName
   * @param courseClass Class for course
   * @param beanClasses Bean的Class数组
   * @return 对等实体的调用方法
   */
  private Method getPeerMethod(final String methodName, final Class<?> courseClass, final Class<?>[] beanClasses) {

    return peerMethodRouter.get(methodName + Arrays.toString(beanClasses), new Callable<Method>() {

      public Method call() throws Exception {
        Method[] methods = methodCache.getInstancePublicMethods(courseClass);

        for (Method method : methods) {
          Class<?>[] params = method.getParameterTypes();
          if (Arrays.equals(params, beanClasses) && method.getName().endsWith(methodName)) {
            return method;
          }
        }

        return null;
      }
    });
  }

  @Override
  public void messageReceived(final Message<Integer, MethodDesc> msg) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        MethodDesc md = msg.getBean();
        Object[] params = md.getParams();

        Class<?>[] classes = ClassUtil.objects2Classes(params);
        Object course = getPeerCourse(md.getMethodName(), classes);
        if (course == null) {
          logger.error("No course class found for {}. Process stopped.", Arrays.toString(classes));
          return;
        }
        try {
          // tpsMonitor.incHandledTransactionStart();
          invokePeerMethod(msg, course, classes, params);
          // tpsMonitor.incHandledTransactionEnd();
        } catch (Exception e) {
          logger.error("invokePeerMethod error.", e);
        }
      }
    };

    receiverExecutor.submit(task);

  }

  /**
   * 调用对等实体方法
   * 
   * @param msg 传输消息 @see TransportMessage
   * @param course 对等实体类 @see PeerCourse
   * @param classes 携带类型数组
   * @param msgs 携带对象数组
   */
  private void invokePeerMethod(Message<Integer, MethodDesc> msg, Object course, Class<?>[] classes, Object... msgs) {
    Method peerMethod = getPeerMethod(msg.getBean().getMethodName(), course.getClass(), classes);
    if (peerMethod == null) {
      logger.error("No peer method found for message {}. No process execute.", Arrays.toString(classes));
      return;
    }

    try {
      Object result = peerMethod.invoke(course, msgs);
      if (MethodUtil.isReturnVoid(peerMethod) || (msg instanceof RpcNotify && !((RpcNotify) msg).isCallback())) {
        // SessionUtil.unbind(msg);
        return;
      }

      sendResult(result, msg);
    } catch (Exception e) {
      logger.error("Invoke peer method [{}] failed.", peerMethod, e);
      sendError(e, msg);
    }

  }

  /**
   * 对等实体方法调用之后的处理
   * 
   * @param result 结果值
   * @param msg 传输消息 @see TransportMessage
   */
  private void sendResult(Object result, Message<Integer, MethodDesc> msg) {
    // FIXME
    PeerSender sender = SessionUtil.getSession(msg);
    if (sender == null) {
      logger.warn("can not found sender,msg=[{}]", msg);
      return;
    }
    if (result == null) {
      result = new NullObject();
    }
    
    RpcResponse<Object> response = new RpcResponse<Object>(result, msg.getId());
    response.setSuccess(true);
    // SessionUtil.unbind(msg);
    sender.send(response);
  }

  private void sendError(Exception e, Message<Integer, MethodDesc> msg) {
    PeerSender sender = SessionUtil.getSession(msg);
    if (sender == null) {
      logger.warn("can not found sender,msg=[{}]", msg);
      return;
    }
    // FIXME isRight? for test
    RpcResponse<Object> response = new RpcResponse<Object>(e, msg.getId());
    response.setError(e);
    // SessionUtil.unbind(msg);
    sender.send(response);
  }

  /**
   * 获取对等实体类 @see PeerCourse
   * 
   * @param name 名称
   * @param classes Class数组
   * @return 对等实体类
   */
  private Object getPeerCourse(String name, Class<?>[] classes) {
    return courseTable.get(name + Arrays.toString(classes));
  }

  /**
   * 设置对等实体类 @see PeerCourse
   * 
   * @param courses 对等实体类集合
   */
  public void setCourses(Collection<Object> courses) {
    for (Object course : courses) {
      setCourse(course);
    }
  }

  public void setCourse(Object course) {
    // FIXME should add className
    Method[] methods = methodCache.getInstancePublicMethods(course.getClass());

    for (Method method : methods) {
      Class<?>[] params = method.getParameterTypes();

      courseTable.put(method.getName() + Arrays.toString(params), course);
      // FIXME
      courseTable.put(Arrays.toString(ClassUtils.wrappersToPrimitives(params)), course);
      courseTable.put(Arrays.toString(ClassUtils.primitivesToWrappers(params)), course);
    }

  }

}
