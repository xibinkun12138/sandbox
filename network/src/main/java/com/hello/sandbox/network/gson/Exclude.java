package com.hello.sandbox.network.gson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 让 Gson 解析时略过所属的 Field 使用 {@link GsonUtils#getGsonWithExclude()}
 *
 * @author yang.yandong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Exclude {}
