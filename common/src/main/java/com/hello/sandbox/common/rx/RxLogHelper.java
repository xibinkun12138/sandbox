package com.hello.sandbox.common.rx;

import android.os.Looper;
import com.hello.sandbox.common.BuildConfig;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RxLogHelper {

  private static Action1<Throwable> reportErrorAction;

  public static void init(Action1<Throwable> action) {
    reportErrorAction = action;
  }

  public static void printSubscribeOnError(Exception e) {
    if (BuildConfig.DEBUG && Looper.myLooper() == Looper.getMainLooper()) {
      if (reportErrorAction != null && e != null) {
        reportErrorAction.call(e);
      }
    }
  }

  /** 给底层各个类库用到，业务代码使用Rxu.subsOn 方法 */
  @Deprecated
  public static <T> Observable.Transformer<T, T> subscribeOnTransformer(Scheduler scheduler) {
    if (!BuildConfig.DEBUG || scheduler == AndroidSchedulers.mainThread()) {
      return o -> o.subscribeOn(scheduler);
    } else {
      Exception e =
          new Exception("run in UI thread after subscribeOn(notUIThread)，Check it before ANR");
      return o ->
          o.subscribeOn(scheduler)
              .map(
                  t -> {
                    printSubscribeOnError(e);
                    return t;
                  });
    }
  }
}
