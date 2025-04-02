package com.hello.sandbox.fake.service;

import black.android.os.BRIDeviceIdentifiersPolicyServiceStub;
import black.android.os.BRServiceManager;
import com.hello.sandbox.SandBoxCore;
import com.hello.sandbox.fake.hook.BinderInvocationStub;
import com.hello.sandbox.fake.hook.MethodHook;
import com.hello.sandbox.fake.hook.ProxyMethod;
import com.hello.sandbox.utils.Md5Utils;
import java.lang.reflect.Method;

/** Created by Milk on 4/3/21. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug */
public class IDeviceIdentifiersPolicyProxy extends BinderInvocationStub {

  public IDeviceIdentifiersPolicyProxy() {
    super(BRServiceManager.get().getService("device_identifiers"));
  }

  @Override
  protected Object getWho() {
    return BRIDeviceIdentifiersPolicyServiceStub.get()
        .asInterface(BRServiceManager.get().getService("device_identifiers"));
  }

  @Override
  protected void inject(Object baseInvocation, Object proxyInvocation) {
    replaceSystemService("device_identifiers");
  }

  @Override
  public boolean isBadEnv() {
    return false;
  }

  @ProxyMethod("getSerialForPackage")
  public static class x extends MethodHook {
    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
      //                args[0] = SandBoxCore.getHostPkg();
      //                return method.invoke(who, args);
      return Md5Utils.md5(SandBoxCore.getHostPkg());
    }
  }
}
