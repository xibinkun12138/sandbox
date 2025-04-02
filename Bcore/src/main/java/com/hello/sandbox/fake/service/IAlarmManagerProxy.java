package com.hello.sandbox.fake.service;

import android.content.Context;
import black.android.app.BRIAlarmManagerStub;
import black.android.os.BRServiceManager;
import java.lang.reflect.Method;
import com.hello.sandbox.fake.hook.BinderInvocationStub;
import com.hello.sandbox.fake.hook.MethodHook;
import com.hello.sandbox.fake.hook.ProxyMethod;

/** Created by Milk on 4/3/21. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug */
public class IAlarmManagerProxy extends BinderInvocationStub {

  public IAlarmManagerProxy() {
    super(BRServiceManager.get().getService(Context.ALARM_SERVICE));
  }

  @Override
  protected Object getWho() {
    return BRIAlarmManagerStub.get()
        .asInterface(BRServiceManager.get().getService(Context.ALARM_SERVICE));
  }

  @Override
  protected void inject(Object baseInvocation, Object proxyInvocation) {
    replaceSystemService(Context.ALARM_SERVICE);
  }

  @ProxyMethod("set")
  public static class Set extends MethodHook {
    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
      return 0;
    }
  }

  @Override
  public boolean isBadEnv() {
    return false;
  }
}
