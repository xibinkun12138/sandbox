package com.hello.sandbox.fake.service.context.providers;

import android.os.Bundle;
import android.os.IInterface;
import android.util.Log;
import black.android.content.BRAttributionSource;
import com.hello.sandbox.app.BActivityThread;
import com.hello.sandbox.fake.hook.ClassInvocationStub;
import com.hello.sandbox.utils.compat.ContextCompat;
import java.lang.reflect.Method;

/** Created by Milk on 4/8/21. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug */
public class ContentProviderStub extends ClassInvocationStub implements BContentProvider {
  public static final String TAG = "ContentProviderStub";
  private IInterface mBase;
  private String mAppPkg;

  public IInterface wrapper(final IInterface contentProviderProxy, final String appPkg) {
    mBase = contentProviderProxy;
    mAppPkg = appPkg;
    injectHook();
    return (IInterface) getProxyInvocation();
  }

  @Override
  protected Object getWho() {
    return mBase;
  }

  @Override
  protected void inject(Object baseInvocation, Object proxyInvocation) {}

  @Override
  protected void onBindMethod() {}

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("asBinder".equals(method.getName())) {
      return method.invoke(mBase, args);
    }
    if (args != null && args.length > 0) {
      Object arg = args[0];
      if (arg instanceof String) {
        args[0] = mAppPkg;
      } else if (arg.getClass().getName().equals(BRAttributionSource.getRealClass().getName())) {
        ContextCompat.fixAttributionSourceState(arg, BActivityThread.getBUid());
      }
    }
    try {
      Object result = method.invoke(mBase, args);
      if (result instanceof Bundle) {
        Log.d(TAG, "result " + result);
      }
      return result;
    } catch (Throwable e) {
      throw e.getCause();
    }
  }

  @Override
  public boolean isBadEnv() {
    return false;
  }
}
