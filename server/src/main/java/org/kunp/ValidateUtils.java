package org.kunp;

public final class ValidateUtils {

  public static void notNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notEmpty(String target) {
    if (target == null || target.isEmpty()) {
      throw new IllegalArgumentException(target);
    }
  }
}
