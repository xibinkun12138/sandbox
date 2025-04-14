package com.hello.sandbox.fake.service.base;

import com.hello.sandbox.utils.compat.BuildCompat;
import com.android.internal.infra.AndroidFuture;
import java.lang.reflect.Method;

/**
 * @author virtual_space
 * @function
 **/
public class AndroidFutureMethodProxy extends ValueMethodProxy{

    public AndroidFutureMethodProxy(String name, Object value) {
        super(name, value);
    }

    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
        Object hook = super.hook(who, method, args);
        if (BuildCompat.isT() || !BuildCompat.isS() || (hook instanceof AndroidFuture)){
            return hook;
        }
        AndroidFuture androidFuture = new AndroidFuture();
        androidFuture.complete(hook);
        return androidFuture;
    }
}
