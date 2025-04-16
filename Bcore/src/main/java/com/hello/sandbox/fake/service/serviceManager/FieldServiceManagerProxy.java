package com.hello.sandbox.fake.service.serviceManager;

import android.os.IInterface;

import com.hello.sandbox.fake.hook.BinderInvocationStub;

import black.android.os.BRServiceManager;
import black.android.os.BRServiceManagerProxy;

public class FieldServiceManagerProxy extends BinderInvocationStub {
    private final IInterface mIInterface;

    public FieldServiceManagerProxy(IInterface iInterface) {
        super(iInterface.asBinder());
        this.mIInterface = iInterface;
    }

    @Override
    public Object getWho() {
        return this.mIInterface;
    }

    @Override
    public void inject(Object obj, Object obj2) {
        BRServiceManagerProxy.get(BRServiceManager.get().sServiceManager())._set_mServiceManager(obj2);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public void onBindMethod() {
        super.onBindMethod();
        addMethodHook("getService", ServiceManagerProxy.instance.hook);
        addMethodHook("checkService", ServiceManagerProxy.instance.hook);
    }

}
