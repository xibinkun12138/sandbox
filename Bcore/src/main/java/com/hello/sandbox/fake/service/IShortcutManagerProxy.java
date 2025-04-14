package com.hello.sandbox.fake.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import black.android.content.pm.BRIShortcutServiceStub;
import black.android.content.pm.BRParceledListSlice;
import black.android.os.BRServiceManager;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.android.internal.infra.AndroidFuture;
import com.hello.sandbox.SandBoxCore;
import com.hello.sandbox.fake.hook.BinderInvocationStub;
import com.hello.sandbox.fake.hook.MethodHook;
import com.hello.sandbox.fake.hook.ProxyMethod;
import com.hello.sandbox.fake.hook.ProxyMethods;
import com.hello.sandbox.fake.hook.ReplacePackageNameMethodHook;
import com.hello.sandbox.fake.service.base.AndroidFutureMethodProxy;
import com.hello.sandbox.fake.service.base.PkgMethodProxy;
import com.hello.sandbox.fake.service.base.ValueMethodProxy;
import com.hello.sandbox.utils.MethodParameterUtils;
import com.hello.sandbox.utils.compat.BuildCompat;
import com.hello.sandbox.utils.compat.ParceledListSliceCompat;

/** Created by Milk on 4/5/21. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug 未实现，全部拦截 */
public class IShortcutManagerProxy extends BinderInvocationStub {

  public IShortcutManagerProxy() {
    super(BRServiceManager.get().getService(Context.SHORTCUT_SERVICE));
  }

  @Override
  protected Object getWho() {
    return BRIShortcutServiceStub.get()
        .asInterface(BRServiceManager.get().getService(Context.SHORTCUT_SERVICE));
  }

  @Override
  protected void inject(Object baseInvocation, Object proxyInvocation) {
    replaceSystemService(Context.SHORTCUT_SERVICE);
  }

  @Override
  public boolean isBadEnv() {
    return false;
  }

  @Override
  protected void onBindMethod() {
    super.onBindMethod();
    addMethodHook(new ValueMethodProxy("getDynamicShortcuts", BRParceledListSlice.get()._new(new ArrayList<>())));
    addMethodHook("getManifestShortcuts", new ReplacePackageNameMethodHook(0));
    addMethodHook("getPinnedShortcuts", new ReplacePackageNameMethodHook(0));
    addMethodHook("disableShortcuts", new ReplacePackageNameMethodHook(0));
    addMethodHook("disableShortcuts", new ReplacePackageNameMethodHook(0));
    addMethodHook("getMaxShortcutCountPerActivity", new ReplacePackageNameMethodHook(0));
    addMethodHook("getRemainingCallCount", new ReplacePackageNameMethodHook(0));
    addMethodHook("getRateLimitResetTime", new ReplacePackageNameMethodHook(0));
    addMethodHook("getIconMaxDimensions", new ReplacePackageNameMethodHook(0));
    addMethodHook("reportShortcutUsed", new ReplacePackageNameMethodHook(0));
    addMethodHook("onApplicationActive", new ReplacePackageNameMethodHook(0));
    addMethodHook(new AndroidFutureMethodProxy("setDynamicShortcuts", false));
    addMethodHook(new AndroidFutureMethodProxy("addDynamicShortcuts", false));
    addMethodHook(new AndroidFutureMethodProxy("removeDynamicShortcuts", true));
    addMethodHook(new AndroidFutureMethodProxy("removeAllDynamicShortcuts", true));
    addMethodHook(new AndroidFutureMethodProxy("pushDynamicShortcut", null));
    if (BuildCompat.isS()) {
      addMethodHook(new AndroidFutureMethodProxy("updateShortcuts", false));
    } else {
      addMethodHook("updateShortcuts", new ReplacePackageNameMethodHook(0));
    }
    addMethodHook("enableShortcuts", new ReplacePackageNameMethodHook(0));
    addMethodHook("getShareTargets", new ReplacePackageNameMethodHook(0));
    addMethodHook("hasShareTargets", new ReplacePackageNameMethodHook(0));
    addMethodHook(new ValueMethodProxy("isRequestPinItemSupported", false));
  }


  @ProxyMethods({"setDynamicShortcuts", "addDynamicShortcuts"})
  public class AndroidFutureFalse extends MethodHook {
    public Object hook(Object proxy, Method method, Object[] args) throws Throwable {
      if (BuildCompat.isOnlyS()) {
        AndroidFuture androidFuture = new AndroidFuture();
        androidFuture.complete(Boolean.FALSE);
        return androidFuture;
      }
      return Boolean.FALSE;
    }
  }


  @ProxyMethods({"createShortcutResultIntent"})
  public static class AndroidFutureIntent extends MethodHook {
    @Override
    public Object hook(Object proxy, Method method, Object[] args) throws Throwable {
      if (!BuildCompat.isOnlyS()) {
        return null;
      }
      AndroidFuture androidFuture = new AndroidFuture();
      androidFuture.complete(new Intent());
      return androidFuture;
    }
  }

  @ProxyMethod("createShortcutResultIntent")
  public static class createShortcutResultIntent extends MethodHook {
    @Override
    public Object hook(Object proxy, Method method, Object[] args) throws Throwable {
      if (args != null) {
        try {
          if (BuildCompat.isT()) {
            if (args.length >= 4 && (args[3] instanceof AndroidFuture)) {
              ((AndroidFuture) args[3]).complete(new Intent());
              return null;
            }
          } else if (BuildCompat.isS()) {
            AndroidFuture androidFuture = (AndroidFuture) AndroidFuture.class.newInstance();
            androidFuture.complete(new Intent());
            return androidFuture;
          }
        } catch (Throwable unused) {
        }
      }
      return method.invoke(proxy, args);
    }
  }

  @ProxyMethods({"getShortcuts", "removeLongLivedShortcuts"})
  public static class getShortcuts extends MethodHook {
    @Override
    public Object hook(Object proxy, Method method, Object[] args) throws Throwable {
      if (args != null && args.length > 2) {
        args[0] = SandBoxCore.getHostPkg();
        args[2] = Integer.valueOf(SandBoxCore.getHostUserId());
      }
      return method.invoke(proxy, args);
    }
  }

  @ProxyMethod("requestPinShortcut")
  public static class requestPinShortcut extends MethodHook {
    @Override
    public Object hook(Object proxy, Method method, Object[] args) throws Throwable {
      if (args != null) {
        try {
          if (BuildCompat.isT() && args.length >= 4 && (args[args.length - 1] instanceof AndroidFuture)) {
            ((AndroidFuture) args[args.length - 1]).complete("");
          }
        } catch (Throwable unused) {
        }
      }
      return Boolean.TRUE;
    }
  }
}
