package com.sm.common.xaio.receiver;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

import com.sm.common.libs.able.Receiver;
import com.sm.common.libs.util.ClassUtil;
import com.sm.common.libs.util.MethodUtil;
import com.sm.common.xaio.Message;
import com.sm.common.xaio.PeerSender;
import com.sm.common.xaio.annotation.PeerMethod;
import com.sm.common.xaio.util.SessionUtil;

/**
 * 请求接收分发处理
 * 
 * @author <a href="chenxu.xc@alibaba-inc.com">xc</a>
 * @version create on 2016年11月22日 下午2:15:58
 */
public class ParamsDispatcher extends ReceiverDispatchSupport implements Receiver<Message<Integer, Object>> {

  /**
   * 获取对等实体的调用方法
   * 
   * @param courseClass Class for course
   * @param beanClasses Bean的Class数组
   * @return 对等实体的调用方法
   */
  private Method getPeerMethod(final Class<?> courseClass, final Class<?>[] beanClasses) {
    return peerMethodRouter.get(Arrays.toString(beanClasses), new Callable<Method>() {

      public Method call() throws Exception {
        Method[] methods = methodCache.getMethods(courseClass, PeerMethod.class);

        for (Method method : methods) {
          Class<?>[] params = method.getParameterTypes();
          if (Arrays.equals(params, beanClasses)) {
            return method;
          }
        }

        return null;
      }
    });
  }

  @Override
  public void messageReceived(final Message<Integer, Object> msg) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        Object message = msg.getBean();
        Object[] messageArray = null;

        if (message.getClass().isArray()) {
          messageArray = (Object[]) message;
        } else {
          // messageArray = ArrayUtil.append(messageArray, message);
          messageArray = ArrayUtils.add(messageArray, message);
        }
        Class<?>[] classes = ClassUtil.objects2Classes(messageArray);
        Object course = getPeerCourse(classes);
        if (course == null) {
          logger.error("No course class found for {}. Process stopped.", Arrays.toString(classes));
          logger.error("msg uid={}", msg.getId());
          return;
        }
        try {
          // tpsMonitor.incHandledTransactionStart();
          invokePeerMethod(msg, course, classes, messageArray);
          // tpsMonitor.incHandledTransactionEnd();
        } catch (Exception e) {
          logger.error("invokePeerMethod error.", e);
        }
      }
    };

    receiverExecutor.submit(task);

  }

  private void invokePeerMethod(Message<Integer, Object> msg, Object course, Class<?>[] classes, Object... msgs) {
    Method peerMethod = getPeerMethod(course.getClass(), classes);
    if (peerMethod == null) {
      logger.error("No peer method found for message {}. No process execute.", Arrays.toString(classes));
      return;
    }

    try {
      Object result = peerMethod.invoke(course, msgs);
      if (MethodUtil.isReturnVoid(peerMethod)) {
        return;
      }

      sendResult(result, msg);
    } catch (Exception e) {
      logger.error("Invoke peer method [{}] failed. ", e, new Object[] {peerMethod.getName()});
    }

  }

  /**
   * 对等实体方法调用之后的处理
   * 
   * @param result 结果值
   * @param msg 传输消息 @see Signal
   */
  private void sendResult(Object result, Message<Integer, Object> msg) {
    PeerSender sender = SessionUtil.getSession(msg);
    Message<Integer, Object> message = new Message<>(1, result, msg.getId());

    sender.send(message);
  }

  /**
   * 获取对等实体类 @see PeerCourse
   * 
   * @param classes Class数组
   * @return 对等实体类
   */
  private Object getPeerCourse(Class<?>[] classes) {
    return courseTable.get(Arrays.toString(classes));
  }

  /**
   * 设置对等实体类 @see PeerCourse
   * 
   * @param courses 对等实体类集合
   */
  public void setCourses(Collection<Object> courses) {
    for (Object course : courses) {
      Method[] methods = methodCache.getMethods(course.getClass(), PeerMethod.class);

      for (Method method : methods) {
        Class<?>[] params = method.getParameterTypes();

        courseTable.put(Arrays.toString(params), course);
        // FIXME
        courseTable.put(Arrays.toString(ClassUtils.wrappersToPrimitives(params)), course);
        courseTable.put(Arrays.toString(ClassUtils.primitivesToWrappers(params)), course);

      }
    }
  }

}
