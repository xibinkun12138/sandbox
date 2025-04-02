package com.hello.sandbox.fake.service;

import android.content.ComponentName;
import black.android.os.BRServiceManager;
import black.android.view.BRIAutoFillManagerStub;
import com.hello.sandbox.SandBoxCore;
import com.hello.sandbox.app.BActivityThread;
import com.hello.sandbox.fake.hook.BinderInvocationStub;
import com.hello.sandbox.fake.hook.MethodHook;
import com.hello.sandbox.fake.hook.ProxyMethod;
import com.hello.sandbox.proxy.ProxyManifest;
import java.lang.reflect.Method;

/** Created by Milk on 4/8/21. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug */
public class IAutofillManagerProxy extends BinderInvocationStub {
  public static final String TAG = "AutofillManagerStub";

  public IAutofillManagerProxy() {
    super(BRServiceManager.get().getService("autofill"));
  }

  @Override
  protected Object getWho() {
    return BRIAutoFillManagerStub.get().asInterface(BRServiceManager.get().getService("autofill"));
  }

  @Override
  protected void inject(Object baseInvocation, Object proxyInvocation) {
    replaceSystemService("autofill");
  }

  @Override
  public boolean isBadEnv() {
    return false;
  }

  @ProxyMethod("startSession")
  public static class StartSession extends MethodHook {

    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
      if (args != null) {
        for (int i = 0; i < args.length; i++) {
          if (args[i] == null) continue;
          if (args[i] instanceof ComponentName) {
            args[i] =
                new ComponentName(
                    SandBoxCore.getHostPkg(),
                    ProxyManifest.getProxyActivity(BActivityThread.getAppPid()));
          }
        }
      }
      return method.invoke(who, args);
    }
  }
}
