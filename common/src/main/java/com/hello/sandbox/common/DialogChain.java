package com.hello.sandbox.common;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import com.hello.sandbox.common.util.ContextHolder;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/** Created by kingty on 2019-12-05. */
public class DialogChain {

  public interface Chain {
    int getPriority();

    void show();

    void showImmediate();

    void showWithPriority(int priority);

    void realShow();

    void dismiss();

    boolean isShowing();

    long getAddTime();
  }

  private DialogChain() {
    ContextHolder.context()
        .registerActivityLifecycleCallbacks(
            new ActivityLifecycleCallbacks() {
              @Override
              public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

              @Override
              public void onActivityStarted(Activity activity) {}

              @Override
              public void onActivityResumed(Activity activity) {
                topContextRef = new WeakReference<>(activity);
              }

              @Override
              public void onActivityPaused(Activity activity) {}

              @Override
              public void onActivityStopped(Activity activity) {}

              @Override
              public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

              @Override
              public void onActivityDestroyed(Activity activity) {
                removeCertainReference(activity);
              }
            });
  }

  private static final DialogChain chain = new DialogChain();

  public static DialogChain getInstance() {
    return chain;
  }

  static final int IMMEDIATE = Integer.MAX_VALUE - 1000;
  static final int NORMAL = 0;
  static final int FLOW =
      Integer.MAX_VALUE; // keep the flow continuance, so make them the biggest priority

  private static final int DEFAULT_INITIAL_CAPACITY = 4;
  private Comparator<Chain> comparator =
      (o1, o2) -> {
        if (o2.getPriority() == o1.getPriority())
          return Long.compare(o1.getAddTime(), o2.getAddTime());
        return o2.getPriority() - o1.getPriority();
      };

  private ArrayMap<WeakReference<Context>, Queue<Chain>> map = new ArrayMap<>();

  private ArrayMap<WeakReference<Context>, Chain> showingMap = new ArrayMap<>();
  private WeakReference<Context> topContextRef;

  private void removeCertainReference(Context context) {
    WeakReference<Context> certainContext = findContext(context);
    if (certainContext != null) {
      map.remove(certainContext);
      showingMap.remove(certainContext);
    }

    for (WeakReference<Context> reference : map.keySet()) {
      if (reference.get() == null) {
        map.remove(reference);
      }
    }
  }

  void removeCertainDialogFromQueue(Context context, Chain chain) {
    if (context == null) {
      context = topContextRef.get();
    }
    WeakReference<Context> contextWeakReference = findOrCreateContext(context);
    Queue<Chain> queue = findOrCreateQueue(contextWeakReference);

    queue.remove(chain);
  }

  private WeakReference<Context> findContext(Context context) {
    for (WeakReference<Context> reference : map.keySet()) {
      if (context == reference.get()) {
        return reference;
      }
    }
    return null;
  }

  private WeakReference<Context> findOrCreateContext(Context context) {
    WeakReference<Context> contextWeakReference = findContext(context);
    if (contextWeakReference == null) contextWeakReference = new WeakReference<>(context);
    return contextWeakReference;
  }

  private Queue<Chain> findOrCreateQueue(WeakReference<Context> contextWeakReference) {
    assertContextNull(contextWeakReference.get());
    if (map.containsKey(contextWeakReference)) {
      return map.get(contextWeakReference);
    } else {
      Queue<Chain> queue = new PriorityQueue<>(DEFAULT_INITIAL_CAPACITY, comparator);
      map.put(contextWeakReference, queue);
      return queue;
    }
  }

  void addChain(Context context, Chain dialog) {
    if (context == null) { // 是一个 act dialog
      context = topContextRef.get();
    }
    WeakReference<Context> contextWeakReference = findOrCreateContext(context);

    if (showingMap.get(contextWeakReference) != null
        && !showingMap.get(contextWeakReference).isShowing()) { // will not come here , over think
      showingMap.remove(contextWeakReference);
    }

    Queue<Chain> queue = findOrCreateQueue(contextWeakReference);
    queue.offer(dialog);

    showNext(context, false);
  }

  /**
   * //展示队列中剩余的dialog，直到队列为空，是一个循环调用
   *
   * @param context
   * @param whenDismiss ,是否是上一个dialog dismiss 后触发的此方法， true为是，false为添加Chain的时候主动触发
   */
  private void showNextWhenDialog(Context context, boolean whenDismiss) {
    WeakReference<Context> contextWeakReference = findOrCreateContext(context);
    if (whenDismiss) {
      // 如果是上一个dialog dismiss 后触发的此方法，先将正在展示的dialog引用remove，因为此时已经没有dialog展示
      if (showingMap.get(contextWeakReference) != null
          && !showingMap.get(contextWeakReference).isShowing()) {
        showingMap.remove(contextWeakReference);
      }
    }

    Queue<Chain> queue = findOrCreateQueue(contextWeakReference);

    Chain head = queue.peek(); // get but not remove
    // 队列里没有dialog了
    if (null == head) {
      return;
    }
    // 队列里还有dialog，如果当前没有dialog正在展示，直接展示head的dialog
    if (null == showingMap.get(contextWeakReference)) {
      queue.poll(); // remove
      head.realShow();
      showingMap.put(contextWeakReference, head);
      return;
    }
    // 队列里还有dialog,如果当前有dialog正在展示，主要针对主动触发的情况,两种情况
    // 1.不是IMMEDIATE，直接return,等待当前dialog dismiss的时候继续调用此方法展示
    // 2.不是IMMEDIATE,直接dismiss当前dialog,继续调用此方法展示,重走上面逻辑
    if (head.getPriority() == IMMEDIATE) {
      Chain showingDialog = showingMap.get(contextWeakReference);
      if (showingDialog != null && showingDialog.isShowing()) {
        showingDialog.dismiss();
      }
    }
  }
  // thread safe cause all action in main thread
  void showNext(Context context, boolean whenDismiss) {
    if (context == null) {
      showNextWhenDialog(topContextRef.get(), whenDismiss);
    } else {
      showNextWhenDialog(context, whenDismiss);
    }
  }

  private void assertContextNull(Context context) {
    if (BuildConfig.DEBUG) {
      if (context == null) throw new IllegalStateException("context should not null here");
    }
  }

  private void logQueue() {
    StringBuilder builder = new StringBuilder();
    builder.append("info:").append("\n");

    for (WeakReference<Context> reference : map.keySet()) {

      if (reference.get() == null) {
        builder.append("this Context is recycle").append("\n");
        continue;
      }
      Queue<Chain> queue = findOrCreateQueue(reference);
      builder.append(reference.get().toString()).append("\n");
      builder.append("dialogs queue: size => ").append(queue.size()).append("\n");

      int i = 0;
      for (Chain chainWeakReference : queue) {
        if (chainWeakReference != null) {
          String type = chainWeakReference instanceof DialogBase ? "dialog" : "ActDialog";
          builder
              .append("-    ")
              .append(i++)
              .append(": type => ")
              .append(type)
              .append("  priority => ")
              .append(chainWeakReference.getPriority())
              .append("\n");
        } else {
          builder.append("-    this chain is null ");
        }
      }
    }
  }
}
