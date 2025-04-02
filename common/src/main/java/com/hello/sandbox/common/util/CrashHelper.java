package com.hello.sandbox.common.util;

import com.hello.sandbox.common.util.collections.Unit;
import rx.Subscriber;
import rx.functions.Action3;
import rx.observers.Subscribers;
import rx.subjects.BehaviorSubject;

public class CrashHelper {
  public enum ReportLevel {
    p0,
    p1,
    p2,
    p3,
    p4,
    p5,
    p6,
    p7,
    p8,
    p9
  }

  private static Action3<Throwable, String, ReportLevel> reportErrorAction;
  private static Action3<Throwable, String, ReportLevel> reportNetErrorAction;
  private static BehaviorSubject<Unit> hasInit = BehaviorSubject.create();
  private static Subscriber<Unit> reportErrorLaterSubscriber = Subscribers.empty();

  public static void init(
      Action3<Throwable, String, ReportLevel> reportErrorAction,
      Action3<Throwable, String, ReportLevel> reportNetErrorAction) {
    CrashHelper.reportErrorAction = reportErrorAction;
    CrashHelper.reportNetErrorAction = reportNetErrorAction;
    hasInit.onNext(Unit.UNIT);
  }

  public static void reportError(Throwable e) {
    if (reportErrorAction != null) {
      reportErrorAction.call(e, null, null);
    }
  }

  public static void reportError(Throwable e, String business, ReportLevel level) {
    if (reportErrorAction != null) {
      reportErrorAction.call(e, business, level);
    }
  }

  public static void reportError(Throwable e, int freq) {
    // report more error if it is debug build, so we catch more error in debug mode
    if (UtilSDk.DEBUG_BUILD) {
      reportError(e);
    } else {
      if (RandUtil.lcroInterval(0, freq) == 0) {
        reportError(e);
      }
    }
  }

  public static void reportError(Throwable e, String business, ReportLevel level, int freq) {
    // report more error if it is debug build, so we catch more error in debug mode
    if (UtilSDk.DEBUG_BUILD) {
      reportError(e, business, level);
    } else {
      if (RandUtil.lcroInterval(0, freq) == 0) {
        reportError(e, business, level);
      }
    }
  }

  public static void reportErrorLater(Throwable e) {
    if (hasInit.hasValue()) { // report now
      reportError(e);
    } else { // report later, because CrashHelper has not init
      reportErrorLaterSubscriber.add(
          hasInit.subscribe(
              u -> {
                reportError(e);
                if (!reportErrorLaterSubscriber.isUnsubscribed()) {
                  reportErrorLaterSubscriber.unsubscribe();
                }
              }));
    }
  }

  public static void reportNetError(Throwable e, String business, ReportLevel level) {
    if (reportNetErrorAction != null) {
      reportNetErrorAction.call(e, business, level);
    }
  }

  public static void reportNetError(Throwable e, String business, ReportLevel level, int freq) {
    // report more error if it is debug build, so we catch more error in debug mode
    if (UtilSDk.DEBUG_BUILD) {
      reportNetError(e, business, level);
    } else {
      if (RandUtil.lcroInterval(0, freq) == 0) {
        reportNetError(e, business, level);
      }
    }
  }
}
