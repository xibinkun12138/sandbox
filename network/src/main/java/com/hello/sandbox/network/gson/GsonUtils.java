package com.hello.sandbox.network.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 维护 Gson 的对象，全局使用默认配置的 gson 的统一对象 保证堆内存的友好性，以及工具方法（按照 gson 现在友好的接口封装，应该不需要工具方法）
 *
 * @author yang.yandong
 */
public class GsonUtils {

  private static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

  private static final Gson gsonWithExclude =
      new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();

  /** 返回一个 Gson 对象 */
  public static Gson getGson() {
    return gson;
  }

  /** 返回支持 {@link Exclude} 标记的 Gson 对象 */
  public static Gson getGsonWithExclude() {
    return gsonWithExclude;
  }

  public static String toJson(Object o) {
    return getGson().toJson(o);
  }

  public static <T> T fromJson(String json, Type typeOfT) {
    return getGson().fromJson(json, typeOfT);
  }

  public static <T> T fromJson(String json, Class<T> classOfT) {
    return getGson().fromJson(json, classOfT);
  }

  /** 在格式错误时不抛异常, 返回null, 为了处理服务器500+的情况, 会返回一个普通字符串 */
  public static <T> T fromJsonIgnoreException(String json, Class<T> classOfT) {
    try {
      return gson.fromJson(json, classOfT);
    } catch (Throwable ignore) {
      return null;
    }
  }

  public static <T> List<T> getDataList(String strJson, Type typeOfT) {
    return gson.fromJson(strJson, typeOfT);
  }

  public static <T> Map<String, T> getDataMap(String strJson, Type typeOfT) {
    return gson.fromJson(strJson, typeOfT);
  }
}
