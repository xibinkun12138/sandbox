package com.hello.sandbox.common.util;

import android.os.Handler;
import android.os.Looper;
import com.hello.sandbox.common.rx.RxLogHelper;
import com.hello.sandbox.common.util.collections.Unit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.RxThreadFactory;
import rx.schedulers.Schedulers;

public class ThreadUtil {

  private static final Handler handler = new Handler(Looper.getMainLooper());

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

  public static void post(Runnable r) {
    handler.post(r);
  }

  public static void removeCallbacks(Runnable r) {
    handler.removeCallbacks(r);
  }

  public static void postDelayed(Runnable r, long delay) {
    handler.postDelayed(r, delay);
  }

  /** *************************************************************************************** */
  private static final int CPU_COUNT = 3 * Runtime.getRuntime().availableProcessors();

  private static final int RUNNABLE_MAXIMUM_SIZE = Math.max(5, CPU_COUNT - 1);

  private static final RemovableList runnableTaskList = new RemovableList();
  private static int runnableTaskCount = 0;
  private static final int MAX_THREAD_COUNT =
      Math.max(5, Runtime.getRuntime().availableProcessors());

  private static final ThreadPoolExecutor executorService;

  static {
    executorService =
        new ThreadPoolExecutor(
            MAX_THREAD_COUNT,
            MAX_THREAD_COUNT,
            8,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new RxThreadFactory("TTIoPool-"));
    executorService.allowCoreThreadTimeOut(true);
  }

  /** @param force true 强制创建子线程; false 如果当前在主线程才会创建子线程，否则直接运行 */
  public static void io(Runnable runnable, boolean force) {
    if (runnable == null) {
      throw new NullPointerException("Runnable could not be null !!!");
    }
    // 检查是否在io线程
    if (isUiThread() || force) {
      // 添加到集合中
      runnableTaskList.addLast(runnable);
      checkExecuteTask();
    } else {
      // 非主线程不加入到任务队列直接执行
      try {
        runnable.run();
      } catch (Throwable e) {
      }
    }
  }

  /** 检查是否还有需要执行的Runnable */
  private static void checkExecuteTask() {
    if (runnableTaskList.size() > 0 && runnableTaskCount < RUNNABLE_MAXIMUM_SIZE) {
      runnableTaskCount++;
      executeRunnableTask();
    }
  }

  /** 执行Runnable */
  private static void executeRunnableTask() {
    Observable.fromCallable(
            () -> {
              while (true) {
                Runnable r = runnableTaskList.pollFirst();
                if (r != null) {
                  try {
                    r.run();
                  } catch (Throwable t) {
                  }
                } else {
                  break;
                }
              }
              post(
                  () -> {
                    if (UtilSDk.DEBUG_BUILD)
                      LogUtils.i("Au.io", "check on thread " + Thread.currentThread().getName());
                    runnableTaskCount--;
                    checkExecuteTask();
                  });
              return Unit.UNIT;
            })
        .compose(RxLogHelper.subscribeOnTransformer(Schedulers.from(executorService)))
        .observeOn(AndroidSchedulers.mainThread())
        .materialize()
        .subscribe();
  }

  private static class RemovableList {
    private final Map<String, Runnable> mapRemovable = new HashMap<>();
    private final LinkedList<Runnable> lst = new LinkedList<>();
    private final PriorityBlockingQueue<Runnable> queue =
        new PriorityBlockingQueue<>(
            11,
            (o1, o2) -> {
              if (o1 instanceof PriorityRunnable && (o2 instanceof PriorityRunnable)) {
                int compare = ((PriorityRunnable) o1).compareTo((PriorityRunnable) o2);
                if (compare == 0) {
                  compare = lst.indexOf(o1) - lst.indexOf(o2);
                }
                return compare;
              }
              if (o1 instanceof PriorityRunnable) {
                return ((PriorityRunnable) o1).priority.priority - 1;
              }
              if (o2 instanceof PriorityRunnable) {
                return 1 - ((PriorityRunnable) o2).priority.priority;
              }
              return lst.indexOf(o1) - lst.indexOf(o2);
            });

    public synchronized void addLast(Runnable r) {
      if (r instanceof RemovedRunnable) {
        RemovedRunnable rm = (RemovedRunnable) r;
        RemovedRunnable removedRunnable = (RemovedRunnable) mapRemovable.get(rm.getId());
        if (removedRunnable != null) {
          lst.remove(removedRunnable);
          queue.remove(removedRunnable);
        }
        mapRemovable.put(rm.getId(), r);
      }
      lst.addLast(r);
      queue.add(r);
    }

    public synchronized int size() {
      return queue.size();
    }

    public synchronized Runnable pollFirst() {
      Runnable result = queue.poll();
      lst.remove(result);
      if (result != null) {
        if (result instanceof RemovedRunnable) {
          RemovedRunnable rm = (RemovedRunnable) result;
          mapRemovable.remove(rm.getId());
        }
      }
      return result;
    }
  }

  public abstract static class RemovedRunnable implements Runnable {
    public abstract String getId();
  }

  public abstract static class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {

    private Priority priority;

    @Override
    public int compareTo(PriorityRunnable o) {
      return this.priority.priority - o.priority.priority;
    }

    public enum Priority {
      HIGH(0),
      NORMAL(1),
      LOW(2);

      private int priority;

      Priority(int o) {
        this.priority = o;
      }
    }

    public PriorityRunnable(Priority priority) {
      this.priority = priority;
    }

    public void setPriority(Priority priority) {
      this.priority = priority;
    }

    public Priority getPriority() {
      return priority;
    }
  }
}
