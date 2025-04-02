package com.hello.sandbox.common.rx;

import android.util.AndroidRuntimeException;
import android.util.Pair;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hello.sandbox.common.Au;
import com.hello.sandbox.common.BuildConfig;
import com.hello.sandbox.common.util.CrashHelper;
import com.hello.sandbox.common.util.LogUtils;
import com.hello.sandbox.common.util.UtilSDk;
import com.hello.sandbox.common.util.collections.Unit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Notification;
import rx.Observable;
import rx.Observable.Transformer;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.MissingBackpressureException;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.functions.Func5;
import rx.functions.Func6;
import rx.functions.Func7;
import rx.functions.Func8;
import rx.functions.Func9;
import rx.functions.FuncN;
import rx.functions.Functions;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;

/** Created by molikto on 08/25/15. */
public class Rxu {

  public static Subscription delayedSubscription(
      Func0<Subscription> org, Scheduler scheduler, int time, TimeUnit unit) {
    return Subscriptions.create(
        new Action0() {
          Subscription subs;
          boolean cancelled = false;

          {
            scheduler
                .createWorker()
                .schedule(
                    () -> {
                      synchronized (this) {
                        if (!cancelled) {
                          subs = org.call();
                        }
                      }
                    },
                    time,
                    unit);
          }

          @Override
          public void call() {
            synchronized (this) {
              cancelled = true;
              if (subs != null) {
                subs.unsubscribe();
              }
            }
          }
        });
  }

  public static <T> Subscriber<T> ignore() {
    return Subscribers.from(
        new Observer<T>() {
          @Override
          public void onCompleted() {}

          @Override
          public void onError(Throwable e) {
            Rxu.onErrorIgnored(e);
          }

          @Override
          public void onNext(T t) {}
        });
  }

  public static <T> Transformer<T, T> itm() {
    Exception e = new Exception("run in UI thread after subscribeOn(io)，Check it before ANR");
    return o ->
        o.subscribeOn(Schedulers.io())
            .map(
                t -> {
                  return t;
                })
            .observeOn(AndroidSchedulers.mainThread());
  }

  public static <T> Transformer<T, T> ctm() {
    Exception e =
        new Exception("run in UI thread after subscribeOn(computation)，Check it before ANR");
    return o ->
        o.subscribeOn(Schedulers.computation())
            .map(
                t -> {
                  return t;
                })
            .observeOn(AndroidSchedulers.mainThread());
  }

  /** subscribeOn(AndroidSchedulers.mainThread())的包装 */
  public static <T> Transformer<T, T> subsOnMain() {
    return RxLogHelper.subscribeOnTransformer(AndroidSchedulers.mainThread());
  }

  /** subscribeOn(Schedulers.io())的包装 */
  public static <T> Transformer<T, T> subsOnIo() {
    return RxLogHelper.subscribeOnTransformer(Schedulers.io());
  }

  /** subscribeOn(Schedulers.computation())的包装 */
  public static <T> Transformer<T, T> subsOnCompute() {
    return RxLogHelper.subscribeOnTransformer(Schedulers.computation());
  }

  public static <T> Transformer<T, Notification<T>> reportThenMaterialize() {
    return o -> o.doOnError(throwable -> LogUtils.e(throwable.getMessage())).materialize();
  }

  public static <T> Transformer<T, Notification<T>> printThenMaterialize() {
    return o -> o.doOnError(throwable -> LogUtils.e(throwable.getMessage())).materialize();
  }

  /**
   * lazy: lazy subscribe, only the first time lifecycle shows true we subscribe to original lazy
   * onNext, only lifecycle is positive onNext is called, it will cache the latest onNext during the
   * negative lifecycle when lifecycle completes, the how observable completes and it is
   * unsubscribed from original
   */
  public static <T> Observable<T> lazyWithLifecycle(
      final Func0<Observable<T>> org,
      final Observable<Pair<Boolean, Boolean>> lifecycle,
      boolean delayUntilTrue) {
    //		if (!delayUntilTrue) {
    //			return lifecycle
    //					.takeFirst(a -> a)
    //					.flatMap(ignore ->
    //							org.call()
    //									.takeUntil(lifecycle.materialize().filter(a -> !a.isOnNext()))
    //					);
    //		} else {
    //			return lifecycle
    //					.takeFirst(a -> a)
    //					.flatMap(ignore ->
    //							org.call().collect()
    //									.takeUntil(lifecycle.materialize().filter(a -> !a.isOnNext()))
    //					);
    //		}
    return Observable.create(new LazyOnSubs<T>(org, lifecycle, delayUntilTrue));
  }

  static class LazyOnSubs<T> implements Observable.OnSubscribe<T> {
    private final Func0<Observable<T>> org;
    private final Observable<Pair<Boolean, Boolean>> predicate;
    private final boolean delayOnNegative;
    boolean called = false;
    public Subscription subs;
    public Subscription subsPredicate;
    T cached = null;
    boolean stopped = false;
    boolean isCompleted;

    public LazyOnSubs(
        Func0<Observable<T>> org,
        Observable<Pair<Boolean, Boolean>> predicate,
        boolean delayOnNegative) {
      this.org = org;
      this.predicate = predicate;
      this.delayOnNegative = delayOnNegative;
    }

    private void unsubscribeAllSafely() {
      cached = null;
      if (subs != null && !subs.isUnsubscribed()) {
        subs.unsubscribe();
      }
      subs = null;
      if (subsPredicate != null && !subsPredicate.isUnsubscribed()) {
        subsPredicate.unsubscribe();
      }
      subsPredicate = null;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
      subsPredicate =
          predicate.subscribe(
              new Subscriber<Pair<Boolean, Boolean>>() {
                @Override
                public void onCompleted() {
                  cached = null;
                  if (subs != null && !subs.isUnsubscribed()) {
                    subs.unsubscribe();
                  }
                  called = true;
                }

                @Override
                public void onError(Throwable e) {
                  cached = null;
                  subscriber.onError(e);
                }

                @Override
                public void onNext(Pair<Boolean, Boolean> b) {
                  stopped = !b.second;
                  if (!called) {
                    called = true;
                    subs =
                        org.call()
                            .subscribe(
                                new Subscriber<T>() {
                                  @Override
                                  public void onCompleted() {
                                    if (cached == null) {
                                      subscriber.onCompleted();
                                    } else {
                                      isCompleted = true;
                                    }
                                  }

                                  @Override
                                  public void onError(Throwable e) {
                                    cached = null;
                                    subscriber.onError(e);
                                  }

                                  @Override
                                  public void onNext(T t) {
                                    if (delayOnNegative && stopped) {
                                      //									if (DatabaseStore.DETAILED_LOGGING)
                                      // Log.i(DatabaseStore.TAG, "recieved in Rxu.lazy lifecycle
                                      // some value cached");
                                      cached = t;
                                    } else {
                                      try {
                                        subscriber.onNext(t);
                                      } catch (Exception e) {
                                        // ignore
                                        onError(e);
                                      }
                                    }
                                  }
                                });
                  }
                  // 修复RX1 泄漏bug
                  // 如果观察者注册时马上反注册，subs 变量此时还没有赋值，无法在 unsubscribeAllSafely 中被反注册，导致泄漏
                  // 所以在 subs 被赋值时额外检查一次
                  if (isUnsubscribed()) {
                    unsubscribeAllSafely();
                    return;
                  }
                  if (stopped) {
                    if (b.first) {
                      if (subs != null && !subs.isUnsubscribed()) {
                        subs.unsubscribe();
                      }
                    }
                  } else {
                    if (cached != null) {
                      if (!isUnsubscribed()) {
                        subscriber.onNext(cached);
                        if (isCompleted) {
                          subscriber.onCompleted();
                        }
                      }
                      cached = null;
                    }
                  }
                }
              });
      subscriber.add(Subscriptions.create(this::unsubscribeAllSafely));
    }
  }

  public static void disposeQuietly(@Nullable Subscription subscription) {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public static Subscriber<? super Object> print() {
    return Subscribers.create(a -> {}, e -> LogUtils.e(e.getMessage()), () -> {});
  }

  /**
   * handle onError if you don't want handle it.
   *
   * @param e
   */
  public static void onErrorIgnored(Throwable e) {
    if (UtilSDk.DEBUG_BUILD) LogUtils.e("putong", "ignored rx error", e);

    // check if ignored error is fatal
    if (UtilSDk.DEBUG_BUILD && e instanceof AndroidRuntimeException) {
      System.exit(1);
    }
  }

  public static <T> ReportObserver<T> ob(Observer<T> observer) {
    return ReportObserver.ob(TAG(), observer);
  }

  public static <T> ReportObserver<T> ob(Observer<T> observer, boolean needReport) {
    return ReportObserver.ob(TAG(), observer, needReport);
  }

  public static <T> ReportObserver<T> ob() {
    return ReportObserver.ob(TAG());
  }

  public static <T> ReportObserver<T> ob(@NonNull Action1<T> onNext) {
    return ReportObserver.ob(TAG(), onNext);
  }

  public static <T> ReportObserver<T> ob(@NonNull Action1<T> onNext, Action1<Throwable> onError) {
    return ReportObserver.ob(TAG(), onNext, onError);
  }

  public static <T> ReportObserver<T> ob(
      @NonNull Action1<T> onNext, Action1<Throwable> onError, boolean needReport) {
    return ReportObserver.ob(TAG(), onNext, onError, needReport);
  }

  public static <T> ReportObserver<T> ob(
      @NonNull Action1<T> onNext, Action1<Throwable> onError, Action0 onCompleted) {
    return ReportObserver.ob(TAG(), onNext, onError, onCompleted, true);
  }

  public static <T> ReportObserver<T> ob(
      @NonNull Action1<T> onNext,
      Action1<Throwable> onError,
      Action0 onCompleted,
      boolean needReport) {
    return ReportObserver.ob(TAG(), onNext, onError, onCompleted, needReport);
  }

  public static StackTraceElement[] TAG() {
    return getTag(new Exception());
  }

  public static StackTraceElement[] getTag(Exception e) {
    StackTraceElement[] trace = e.getStackTrace();
    StackTraceElement[] result;
    if (trace == null || trace.length <= 2) {
      return null;
    }
    if (BuildConfig.DEBUG) {
      result = new StackTraceElement[1];
      result[0] = trace[2];
      return result;
    }
    result = new StackTraceElement[3];
    result[0] = trace[0];
    result[1] = trace[1];
    result[2] = trace[2];
    return result;
  }

  public static <T1, T2, R> Observable<R> combineLatest(
      Observable<? extends T1> o1,
      Observable<? extends T2> o2,
      Func2<? super T1, ? super T2, ? extends R> combineFunction) {
    return combineLatest(Arrays.asList(o1, o2), Functions.fromFunc(combineFunction), TAG());
  }

  public static <T1, T2, T3, R> Observable<R> combineLatest(
      Observable<? extends T1> o1,
      Observable<? extends T2> o2,
      Observable<? extends T3> o3,
      Func3<? super T1, ? super T2, ? super T3, ? extends R> combineFunction) {
    return combineLatest(Arrays.asList(o1, o2, o3), Functions.fromFunc(combineFunction), TAG());
  }

  public static <T1, T2, T3, T4, R> Observable<R> combineLatest(
      Observable<? extends T1> o1,
      Observable<? extends T2> o2,
      Observable<? extends T3> o3,
      Observable<? extends T4> o4,
      Func4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> combineFunction) {
    return combineLatest(Arrays.asList(o1, o2, o3, o4), Functions.fromFunc(combineFunction), TAG());
  }

  public static <T1, T2, T3, T4, T5, R> Observable<R> combineLatest(
      Observable<? extends T1> o1,
      Observable<? extends T2> o2,
      Observable<? extends T3> o3,
      Observable<? extends T4> o4,
      Observable<? extends T5> o5,
      Func5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R>
          combineFunction) {
    return combineLatest(
        Arrays.asList(o1, o2, o3, o4, o5), Functions.fromFunc(combineFunction), TAG());
  }

  public static <T1, T2, T3, T4, T5, T6, R> Observable<R> combineLatest(
      Observable<? extends T1> o1,
      Observable<? extends T2> o2,
      Observable<? extends T3> o3,
      Observable<? extends T4> o4,
      Observable<? extends T5> o5,
      Observable<? extends T6> o6,
      Func6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R>
          combineFunction) {
    return combineLatest(
        Arrays.asList(o1, o2, o3, o4, o5, o6), Functions.fromFunc(combineFunction), TAG());
  }

  public static <T1, T2, T3, T4, T5, T6, T7, R> Observable<R> combineLatest(
      Observable<? extends T1> o1,
      Observable<? extends T2> o2,
      Observable<? extends T3> o3,
      Observable<? extends T4> o4,
      Observable<? extends T5> o5,
      Observable<? extends T6> o6,
      Observable<? extends T7> o7,
      Func7<
              ? super T1,
              ? super T2,
              ? super T3,
              ? super T4,
              ? super T5,
              ? super T6,
              ? super T7,
              ? extends R>
          combineFunction) {
    return combineLatest(
        Arrays.asList(o1, o2, o3, o4, o5, o6, o7), Functions.fromFunc(combineFunction), TAG());
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Observable<R> combineLatest(
      Observable<? extends T1> o1,
      Observable<? extends T2> o2,
      Observable<? extends T3> o3,
      Observable<? extends T4> o4,
      Observable<? extends T5> o5,
      Observable<? extends T6> o6,
      Observable<? extends T7> o7,
      Observable<? extends T8> o8,
      Func8<
              ? super T1,
              ? super T2,
              ? super T3,
              ? super T4,
              ? super T5,
              ? super T6,
              ? super T7,
              ? super T8,
              ? extends R>
          combineFunction) {
    return combineLatest(
        Arrays.asList(o1, o2, o3, o4, o5, o6, o7, o8), Functions.fromFunc(combineFunction), TAG());
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Observable<R> combineLatest(
      Observable<? extends T1> o1,
      Observable<? extends T2> o2,
      Observable<? extends T3> o3,
      Observable<? extends T4> o4,
      Observable<? extends T5> o5,
      Observable<? extends T6> o6,
      Observable<? extends T7> o7,
      Observable<? extends T8> o8,
      Observable<? extends T9> o9,
      Func9<
              ? super T1,
              ? super T2,
              ? super T3,
              ? super T4,
              ? super T5,
              ? super T6,
              ? super T7,
              ? super T8,
              ? super T9,
              ? extends R>
          combineFunction) {
    return combineLatest(
        Arrays.asList(o1, o2, o3, o4, o5, o6, o7, o8, o9),
        Functions.fromFunc(combineFunction),
        TAG());
  }

  public static <T, R> Observable<R> combineLatest(
      Iterable<? extends Observable<? extends T>> sources, FuncN<? extends R> combineFunction) {
    final boolean[] hasNextError = {false};
    StackTraceElement[] tag = TAG();
    Observable observable =
        Observable.combineLatest(sources, combineFunction)
            .doOnNext(r -> hasNextError[0] = true)
            .doOnCompleted(
                () -> {
                  if (!hasNextError[0]) {
                    RuntimeException e = new RuntimeException("combineLatest never emit next");
                    e.setStackTrace(tag);
                    CrashHelper.reportError(e);
                  }
                });
    return observable;
  }

  public static <T, R> Observable<R> combineLatest(
      List<? extends Observable<? extends T>> sources, FuncN<? extends R> combineFunction) {
    return combineLatest(sources, combineFunction, TAG());
  }

  private static <T, R> Observable<R> combineLatest(
      List<? extends Observable<? extends T>> sources,
      FuncN<? extends R> combineFunction,
      StackTraceElement[] tag) {
    final boolean[] hasNextError = {false};
    Observable observable =
        Observable.combineLatest(sources, combineFunction)
            .doOnNext(r -> hasNextError[0] = true)
            .doOnCompleted(
                () -> {
                  if (!hasNextError[0]) {
                    RuntimeException e =
                        new RuntimeException("combineLatest never emit next" + "\n");
                    e.setStackTrace(tag);
                    CrashHelper.reportError(e);
                  }
                });
    return observable;
  }

  public static <T> Transformer<T, T> safeView(@NonNull View view) {
    PublishSubject<Unit> safeViewBs = PublishSubject.create();
    OnAttachStateChangeListener listener =
        new OnAttachStateChangeListener() {
          @Override
          public void onViewAttachedToWindow(View v) {}

          @Override
          public void onViewDetachedFromWindow(View v) {
            safeViewBs.onNext(Unit.UNIT);
          }
        };
    return o ->
        o.doOnSubscribe(
                () -> {
                  view.addOnAttachStateChangeListener(listener);
                })
            .doOnUnsubscribe(
                () -> {
                  view.removeOnAttachStateChangeListener(listener);
                })
            .takeUntil(safeViewBs);
  }

  public static class ReportObserver<T> implements Observer<T> {

    public static final String DEFAULT = "default_id";
    private String TAG = this.getClass().getSimpleName();

    private StackTraceElement[] id;
    private Action1<T> onNext;
    private Action0 onCompleted;
    private Action1<Throwable> onError;
    private boolean needReport;
    private Observer<T> observer;

    static <T> ReportObserver<T> ob(StackTraceElement[] id) {
      return new ReportObserver<>(id);
    }

    static <T> ReportObserver<T> ob(
        @NonNull StackTraceElement[] id, @NonNull Observer<T> observer) {
      return new ReportObserver<>(id, observer);
    }

    static <T> ReportObserver<T> ob(
        @NonNull StackTraceElement[] id, @NonNull Observer<T> observer, boolean needReport) {
      return new ReportObserver<>(id, observer, needReport);
    }

    static <T> ReportObserver<T> ob(@NonNull StackTraceElement[] id, @NonNull Action1<T> onNext) {
      return new ReportObserver<>(id, onNext);
    }

    static <T> ReportObserver<T> ob(
        @NonNull StackTraceElement[] id, @NonNull Action1<T> onNext, Action1<Throwable> onError) {
      return new ReportObserver<>(id, onNext, onError);
    }

    static <T> ReportObserver<T> ob(
        @NonNull StackTraceElement[] id,
        @NonNull Action1<T> onNext,
        Action1<Throwable> onError,
        boolean needReport) {
      return new ReportObserver<>(id, onNext, onError, needReport);
    }

    static <T> ReportObserver<T> ob(
        @NonNull StackTraceElement[] id,
        @NonNull Action1<T> onNext,
        Action1<Throwable> onError,
        Action0 onCompleted,
        boolean needReport) {
      return new ReportObserver<>(id, onNext, onError, onCompleted, needReport);
    }

    private ReportObserver(@NonNull StackTraceElement[] id) {
      this(id, t -> {});
    }

    private ReportObserver(@NonNull StackTraceElement[] id, Observer<T> observer) {
      this.id = id;
      this.observer = observer;
      this.needReport = true;
    }

    private ReportObserver(
        @NonNull StackTraceElement[] id, Observer<T> observer, boolean needReport) {
      this.id = id;
      this.observer = observer;
      this.needReport = needReport;
    }

    private ReportObserver(@NonNull StackTraceElement[] id, @NonNull Action1<T> onNext) {
      this(id, onNext, null);
    }

    private ReportObserver(
        @NonNull StackTraceElement[] id, @NonNull Action1<T> onNext, Action1<Throwable> onError) {
      this(id, onNext, onError, null, true);
    }

    private ReportObserver(
        @NonNull StackTraceElement[] id,
        @NonNull Action1<T> onNext,
        Action1<Throwable> onError,
        boolean needReport) {
      this(id, onNext, onError, null, needReport);
    }

    private ReportObserver(
        @NonNull StackTraceElement[] id,
        @NonNull Action1<T> onNext,
        Action1<Throwable> onError,
        Action0 onCompleted,
        boolean needReport) {
      this.id = id;
      this.onNext = onNext;
      this.onCompleted = onCompleted;
      this.onError = onError;
      this.needReport = needReport;
    }

    @Override
    public void onCompleted() {
      if (observer != null) {
        observer.onCompleted();
      } else if (onCompleted != null) {
        onCompleted.call();
      }
    }

    @Override
    public void onError(Throwable e) {
      Throwable throwable;
      if (e instanceof MissingBackpressureException) {
        LogUtils.e(
            TAG, "============================================================================");
        if (id != null) {
          LogUtils.e(TAG, "catch a error id = " + id[0].toString());
        }
        LogUtils.e(
            TAG, "============================================================================");

        // 让id在最后一个cause里，把e.getStackTrace()放到中间的cause里
        Throwable t = new MissingBackpressureThrowable("");
        t.setStackTrace(id);

        throwable = new MissingBackpressureThrowable("");
        throwable.initCause(t);
        throwable.setStackTrace(e.getStackTrace());

      } else {
        throwable = new Throwable(e);
        throwable.setStackTrace(id);
      }
      if (observer != null) {
        observer.onError(e);
        if (needReport) {
          reportErrorInProduct(throwable);
        }
      } else if (onError != null) {
        onError.call(e);
        if (needReport) {
          reportErrorInProduct(throwable);
        }
      } else {
        Au.post(
            () -> {
              RuntimeException runtimeException = new RuntimeException(throwable);
              runtimeException.setStackTrace(id);
              throw runtimeException;
            });
      }
    }

    private void reportErrorInProduct(Throwable e) {
      if (!BuildConfig.DEBUG) {
        CrashHelper.reportError(e);
      }
    }

    @Override
    public void onNext(T t) {
      if (observer != null) {
        observer.onNext(t);
      } else if (onNext != null) {
        onNext.call(t);
      }
    }
  }
}
