package org.abubusoft.mee.support;

import org.abubusoft.mee.exceptions.MMERuntimeException;

public abstract class AssertMME {
  private AssertMME() {

  }

  public static void assertTrue(boolean condition, String message, Object ... params) {
    if (!condition) {
      throw new MMERuntimeException(String.format(message, params));
    }
  }

  public static void fail(String message, Object ... params) {
    assertTrue(false, message, params);
  }
}
