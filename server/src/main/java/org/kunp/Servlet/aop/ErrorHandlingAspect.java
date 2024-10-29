package org.kunp.Servlet.aop;

import java.lang.reflect.Method;

public class ErrorHandlingAspect {
  public static void handleError(Exception e) {
    System.err.println("Error occurred: " + e.getMessage());
  }

  public static void invokeWithHandling(Object obj, String methodName) {
    try {
      Method method = obj.getClass().getMethod(methodName);
      if (method.isAnnotationPresent(CatchGlobally.class)) {
        try {
          method.invoke(obj);
        } catch (Exception e) {
          handleError(e);
          System.out.println("핸들러");
        }
      } else {
        method.invoke(obj);
      }
    } catch (Exception e) {
      handleError(e);
    }
  }
}
