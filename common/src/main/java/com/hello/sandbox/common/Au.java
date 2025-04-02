package com.hello.sandbox.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.hello.sandbox.common.util.ContextHolder;
import com.hello.sandbox.common.util.ThreadUtil;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Activity utils
 *
 * <p>contains async operations you want to perform relating to main thread...
 */
public class Au {

  private static HandlerThread subHandlerThread = new HandlerThread("sandbox_sub_thread");
  private static final Handler handler = new Handler(Looper.getMainLooper());
  private static Handler subThreadHandler;
  private static final ConcurrentHashMap<Runnable, Subscription> cachedRunningObservables =
      new ConcurrentHashMap<>();

  public static void runOnUiThread(Runnable r) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      r.run();
    } else {
      handler.post(r);
    }
  }

  public static boolean isUiThread() {
    return Looper.getMainLooper() == Looper.myLooper();
  }

  public static void postDelayed(Context context, Runnable r, long delay) {
    if (r == null) {
      return;
    }
    handler.postDelayed(r, delay);
  }

  private static Observable<Long> createDelayedObs(long delay) {
    return Observable.timer(delay, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread());
  }

  public static void post(Runnable r) {
    handler.post(r);
  }

  public static void post(Context context, Runnable r) {
    if (r == null) {
      return;
    }
    handler.post(r);
  }

  public static void removeCallbacks(Runnable r) {
    if (r == null) {
      return;
    }
    if (cachedRunningObservables.containsKey(r)) {
      cachedRunningObservables.remove(r).unsubscribe();
    } else {
      handler.removeCallbacks(r);
    }
  }

  // 只提供给act的postDelay
  public static boolean containsCallbacks(Runnable r) {
    if (r == null) {
      return false;
    }
    return cachedRunningObservables.containsKey(r);
  }

  public static boolean betweenAndroidVersions(int min, int max) {
    return Build.VERSION.SDK_INT >= min && Build.VERSION.SDK_INT <= max;
  }

  public static void copyIntentDataTypeAndExtra(Intent intent, Intent shareIntent) {
    if (shareIntent != null) {
      if (shareIntent.getExtras() != null) {
        // 如果extra为空，则会crash, SelectContactAct当使用Action_Send启动时，extra可能为空，导致crash
        intent.putExtras(shareIntent.getExtras());
      }
      intent.setAction(shareIntent.getAction());
      intent.setDataAndType(shareIntent.getData(), shareIntent.getType());
    }
  }

  public static void io(Runnable runnable) {
    io(runnable, true);
  }

  /** @param force true 强制创建子线程; false 如果当前在主线程才会创建子线程，否则直接运行 */
  public static void io(Runnable runnable, boolean force) {
    ThreadUtil.io(runnable, force);
  }

  private static Map<String, Observable<Long>> mapTimeOutObs = new HashMap<>();

  /** 指定时间倒计时 */
  public static Observable<Long> timeOutObs(String id, int time) {
    Observable<Long> obs = mapTimeOutObs.get(id);
    if (obs == null) {
      obs =
          Observable.interval(1, TimeUnit.SECONDS)
              .onBackpressureLatest()
              .take(time)
              .observeOn(AndroidSchedulers.mainThread())
              .doOnCompleted(() -> mapTimeOutObs.remove(id))
              .cacheWithInitialCapacity(1);
      mapTimeOutObs.put(id, obs);
    }
    return obs;
  }

  public static Observable<Long> timeOutObs(String id, int initialDelay, int time) {
    Observable<Long> obs = mapTimeOutObs.get(id);
    if (obs == null) {
      obs =
          Observable.interval(initialDelay, 1, TimeUnit.SECONDS)
              .onBackpressureLatest()
              .take(time)
              .observeOn(AndroidSchedulers.mainThread())
              .doOnCompleted(() -> mapTimeOutObs.remove(id))
              .cacheWithInitialCapacity(1);
      mapTimeOutObs.put(id, obs);
    }
    return obs;
  }

  /** 倒计时是否存在 */
  public static boolean isExistTimeOutObs(String id) {
    return null != mapTimeOutObs.get(id);
  }

  /** 去除倒计时 */
  public static void removeTimeOutObs(String id) {
    mapTimeOutObs.remove(id);
  }

  private static final int VIVO_NOTCH = 0x00000020; // 是否有刘海

  public static boolean hasNotchAtOPPO() {
    if (!Build.BRAND.toLowerCase().contains("oppo")) {
      return false;
    }
    return ContextHolder.context()
        .getPackageManager()
        .hasSystemFeature("com.oppo.feature.screen.heteromorphism");
  }

  public static boolean hasNotchAtVivo() {
    if (!Build.BRAND.toLowerCase().contains("vivo")) {
      return false;
    }
    boolean result = false;
    try {
      ClassLoader classLoader = ContextHolder.context().getClassLoader();
      Class FtFeature = classLoader.loadClass("android.util.FtFeature");
      Method method = FtFeature.getMethod("isFeatureSupport", int.class);
      result = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
    } catch (ClassNotFoundException ignore) {
    } catch (NoSuchMethodException ignore) {
    } finally {
      return result;
    }
  }

  /** （此方法只用于修改hostname后）结束自己的进程并重启App */
  public static void restartApp(Context context, Class<?> cls) {
    Intent intent = new Intent(context, cls);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    if (mgr != null) {
      mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent);
      killSelf();
    } else {
      // 理论上不会走到这个case
      Au.postDelayed(context, Au::killSelf, 2000);
    }
  }

  /** （此方法只用于修改hostname后）结束掉自己的进程 */
  public static void killSelf() {
    android.os.Process.killProcess(android.os.Process.myPid());
    System.exit(0);
  }

  public static boolean isAppOnForeground() {
    return !AppFrontBackManager.Companion.getInstance().isBackground();
  }

  public abstract static class RemovedRunnable implements Runnable {

    public abstract String getId();
  }

  public static synchronized void postOnSubThread(Runnable runnable) {
    if (subThreadHandler == null) {
      synchronized (Au.class) {
        if (subThreadHandler == null) {
          subHandlerThread.start();
          subThreadHandler = new Handler(subHandlerThread.getLooper());
        }
      }
    }
    subThreadHandler.post(runnable);
  }
}
