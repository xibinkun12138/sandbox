package com.hello.sandbox.fake.service.base;

import com.hello.sandbox.SandBoxCore;
import com.hello.sandbox.app.BActivityThread;
import com.hello.sandbox.fake.hook.MethodHook;
import java.lang.reflect.Method;

/** Created by BlackBox on 2022/3/5. */
public class UidMethodProxy extends MethodHook {
  private final int index;
  private final String name;

  public UidMethodProxy(String name, int index) {
    this.index = index;
    this.name = name;
  }

  @Override
  protected String getMethodName() {
    return name;
  }

  @Override
  protected Object hook(Object who, Method method, Object[] args) throws Throwable {
    int uid = (int) args[index];
    if (uid == BActivityThread.getBUid()) {
      args[index] = SandBoxCore.getHostUid();
    }
    return method.invoke(who, args);
  }
}
