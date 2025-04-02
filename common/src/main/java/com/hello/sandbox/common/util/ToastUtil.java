package com.hello.sandbox.common.util;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import java.lang.reflect.Field;
import org.joor.Reflect;

public class ToastUtil {
  public interface Delegate {
    void message(int res);

    void message(String t);

    void alert(int res);

    void alert(String t);
  }

  private static class DefaultDelegate implements Delegate {
    @Override
    public void message(int res) {
      message(ContextHolder.context().getText(res).toString());
    }

    @Override
    public void message(String t) {
      systemToast(t);
    }

    @Override
    public void alert(int res) {
      message(res);
    }

    @Override
    public void alert(String t) {
      message(t);
    }
  }

  private static Delegate delegate;

  public static void setDelegate(Delegate delegate) {
    ToastUtil.delegate = delegate;
  }

  private static Delegate get() {
    if (delegate == null) {
      delegate = new DefaultDelegate();
    }
    return delegate;
  }

  public static void message(int res) {
    get().message(res);
  }

  public static void message(String t) {
    get().message(t);
  }

  public static void alert(int res) {
    get().alert(res);
  }

  public static void alert(String t) {
    get().alert(t);
  }

  /** 系统 toast. */
  public static void systemToast(String message) {
    Toast toast = Toast.makeText(ContextHolder.context(), message, Toast.LENGTH_SHORT);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      hook(toast);
    }
    toast.show();
  }

  // 8.0以下手机进行保护
  private static Field tnFiled;
  private static Field tnHandler;

  public static Toast buildToast() {
    Toast toast = new Toast(ContextHolder.context());
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      hook(toast);
    }
    return toast;
  }

  private static void hook(Toast toast) {
    try {
      if (tnFiled == null) {
        tnFiled = Reflect.on("android.widget.Toast").field0("mTN");
      }
      if (tnHandler == null) {
        tnHandler = tnFiled.getType().getDeclaredField("mHandler");
        tnHandler.setAccessible(true);
      }

      if (tnFiled == null || tnHandler == null) {
        return;
      }

      Object tn = tnFiled.get(toast);
      Handler preHandler = (Handler) tnHandler.get(tn);
      tnHandler.set(tn, new SafelyHandlerWrapper(preHandler));
    } catch (Exception e) {
      CrashHelper.reportError(e);
    }
  }

  private static class SafelyHandlerWrapper extends Handler {

    private final Handler impl;

    public SafelyHandlerWrapper(Handler impl) {
      this.impl = impl;
    }

    @Override
    public void dispatchMessage(Message msg) {
      try {
        impl.dispatchMessage(msg);
      } catch (Exception ignored) {
        // catch BadTokenException
      }
    }

    @Override
    public void handleMessage(Message msg) {
      impl.handleMessage(msg);
    }
  }
}
