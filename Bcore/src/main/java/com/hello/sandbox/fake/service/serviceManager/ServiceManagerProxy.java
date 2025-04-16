package com.hello.sandbox.fake.service.serviceManager;

import android.os.IBinder;
import android.os.IInterface;
import android.text.TextUtils;

import com.hello.sandbox.fake.hook.MethodHook;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import black.android.os.BRServiceManager;
import black.android.os.BRServiceManagerProxy;

/**
 * @author virtual_space
 * @function
 **/
public class ServiceManagerProxy {
    public static ServiceManagerProxy instance = new ServiceManagerProxy();
    private final Map<String, IBinder> sCache = new ConcurrentHashMap();
    public MethodHook hook = new MethodHook() {
        @Override
        public Object hook(Object obj, Method method, Object[] objArr) throws Throwable {
            if (objArr != null) {
                String str = (String) objArr[0];
                if (!TextUtils.isEmpty(str)) {
                    IBinder iBinder = (IBinder) ServiceManagerProxy.this.sCache.get(str);
                    if (iBinder != null) {
                        return iBinder;
                    }
                    Object invoke = method.invoke(obj, objArr);
                    if (invoke instanceof IBinder) {
                        ServiceManagerProxy.this.sCache.put(str, (IBinder) invoke);
                    }
                    return invoke;
                }
            }
            return method.invoke(obj, objArr);
        }
    };

    private ServiceManagerProxy() {
    }

    public void init() {
        this.sCache.putAll(BRServiceManager.get().sCache());
        IInterface sServiceManager = BRServiceManager.get().sServiceManager();
        new FieldRemoteProxy(BRServiceManagerProxy.get(sServiceManager).mRemote()).injectHook();
        IInterface mServiceManager = BRServiceManagerProxy.get(sServiceManager).mServiceManager();
        if (mServiceManager != null) {
            new FieldServiceManagerProxy(mServiceManager).injectHook();
        }
    }
}
