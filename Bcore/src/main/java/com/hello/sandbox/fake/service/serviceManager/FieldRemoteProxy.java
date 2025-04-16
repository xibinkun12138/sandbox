package com.hello.sandbox.fake.service.serviceManager;

import android.os.IBinder;

import com.hello.sandbox.fake.hook.BinderInvocationStub;

import black.android.os.BRServiceManager;
import black.android.os.BRServiceManagerProxy;


/**
 * @author virtual_space
 * @function
 **/
public class FieldRemoteProxy extends BinderInvocationStub {

    private IBinder mBaseBinder;

    public FieldRemoteProxy(IBinder baseBinder) {
        super(baseBinder);
        this.mBaseBinder = baseBinder;

    }

    @Override
    protected Object getWho() {
        return this.mBaseBinder;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        BRServiceManagerProxy.get(BRServiceManager.get().sServiceManager())._set_mRemote(proxyInvocation);
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
