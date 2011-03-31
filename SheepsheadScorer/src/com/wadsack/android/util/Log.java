package com.wadsack.android.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Provides Android logging when the Android library is loaded and gracefully
 * degrades back to console logging when not loaded. Allows using this in
 * tests that don't depend on Android environment.
 *
 * Author: Jeremy Wadsack
 */
public class Log {
  private static final SimpleDateFormat dateFormat =
      new SimpleDateFormat("MMM d, yyyy, h:mm a");
  private static Class androidLogClass = null;

  static {
    try {
      androidLogClass = Class.forName("android.util.Log");
    } catch (ClassNotFoundException ignore) {}
  }

  public static void d (String tag, String message) {
    invokeOrPrint("d", "DEBUG", tag, message);
  }

  public static void w (String tag, String message) {
    invokeOrPrint("w", "WARNING", tag, message);
  }

  public static void e (String tag, String message) {
    invokeOrPrint("e", "ERROR", tag, message);
  }

  private static void invokeOrPrint(String method, String level, String tag, String message) {
    boolean messaged = false;

    if (androidLogClass != null) {
      try {
        Method d = androidLogClass.getMethod(
            method,
            new Class[]{String.class, String.class}
        );
        messaged = true;
        d.invoke(null, tag, message);
      } catch (NoSuchMethodException ignore) {
      } catch (InvocationTargetException ignore) {
      } catch (IllegalAccessException ignore) {
      }
    }
    if (!messaged) {
      System.err.println(
          dateFormat.format(Calendar.getInstance().getTime()) +
          level +
          ": " +
          tag +
          " " +
          message);
    }
  }
}
