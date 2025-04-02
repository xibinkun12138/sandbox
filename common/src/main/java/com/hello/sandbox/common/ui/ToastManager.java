/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hello.sandbox.common.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

class ToastManager {

  static final int MSG_TIMEOUT = 0;

  private static final int SHORT_DURATION_MS = 2000;
  private static final int LONG_DURATION_MS = 3500;

  private static ToastManager toastManager;

  static ToastManager getInstance() {
    if (toastManager == null) {
      toastManager = new ToastManager();
    }
    return toastManager;
  }

  @NonNull private final Object lock;
  @NonNull private final Handler handler;

  @Nullable private ToastRecord currentToast;
  @Nullable private ToastRecord nextToast;

  private ToastManager() {
    lock = new Object();
    handler =
        new Handler(
            Looper.getMainLooper(),
            new Handler.Callback() {
              @Override
              public boolean handleMessage(@NonNull Message message) {
                switch (message.what) {
                  case MSG_TIMEOUT:
                    handleTimeout((ToastRecord) message.obj);
                    return true;
                  default:
                    return false;
                }
              }
            });
  }

  interface Callback {
    void show();

    void dismiss(int event);
  }

  public void show(int duration, Callback callback) {
    synchronized (lock) {
      if (isCurrentToastLocked(callback)) {
        // Means that the callback is already in the queue. We'll just update the duration
        currentToast.duration = duration;

        // If this is the Toast currently being shown, call re-schedule it's
        // timeout
        handler.removeCallbacksAndMessages(currentToast);
        scheduleTimeoutLocked(currentToast);
        return;
      } else if (isNextToastLocked(callback)) {
        // We'll just update the duration
        nextToast.duration = duration;
      } else {
        // Else, we need to create a new record and queue it
        nextToast = new ToastRecord(duration, callback);
      }

      if (currentToast != null
          && cancelToastLocked(currentToast, CustomToast.DISMISS_EVENT_CONSECUTIVE)) {
        // If we currently have a Toast, try and cancel it and wait in line
        return;
      } else {
        // Clear out the current toast
        currentToast = null;
        // Otherwise, just show it now
        showNextToastLocked();
      }
    }
  }

  public void dismiss(Callback callback, int event) {
    synchronized (lock) {
      if (isCurrentToastLocked(callback)) {
        cancelToastLocked(currentToast, event);
      } else if (isNextToastLocked(callback)) {
        cancelToastLocked(nextToast, event);
      }
    }
  }

  /**
   * Should be called when a Toast is no longer displayed. This is after any exit animation has
   * finished.
   */
  public void onDismissed(Callback callback) {
    synchronized (lock) {
      if (isCurrentToastLocked(callback)) {
        // If the callback is from a Toast currently show, remove it and show a new one
        currentToast = null;
        if (nextToast != null) {
          showNextToastLocked();
        }
      }
    }
  }

  /**
   * Should be called when a Toast is being shown. This is after any entrance animation has
   * finished.
   */
  public void onShown(Callback callback) {
    synchronized (lock) {
      if (isCurrentToastLocked(callback)) {
        scheduleTimeoutLocked(currentToast);
      }
    }
  }

  private static class ToastRecord {
    @NonNull final Callback callback;
    int duration;

    ToastRecord(int duration, @NotNull Callback callback) {
      this.callback = callback;
      this.duration = duration;
    }

    boolean isToast(@Nullable Callback callback) {
      return this.callback == callback;
    }
  }

  private void showNextToastLocked() {
    if (nextToast != null) {
      currentToast = nextToast;
      nextToast = null;

      final Callback callback = currentToast.callback;
      callback.show();
    }
  }

  private boolean cancelToastLocked(@NonNull ToastRecord record, int event) {
    final Callback callback = record.callback;
    // Make sure we remove any timeouts for the ToastRecord
    handler.removeCallbacksAndMessages(record);
    callback.dismiss(event);
    return true;
  }

  private boolean isCurrentToastLocked(Callback callback) {
    return currentToast != null && currentToast.isToast(callback);
  }

  private boolean isNextToastLocked(Callback callback) {
    return nextToast != null && nextToast.isToast(callback);
  }

  private void scheduleTimeoutLocked(@NonNull ToastRecord r) {

    int durationMs = LONG_DURATION_MS;
    if (r.duration > 0) {
      durationMs = r.duration;
    } else if (r.duration == CustomToast.LENGTH_SHORT) {
      durationMs = SHORT_DURATION_MS;
    }
    handler.removeCallbacksAndMessages(r);
    handler.sendMessageDelayed(Message.obtain(handler, MSG_TIMEOUT, r), durationMs);
  }

  void handleTimeout(@NonNull ToastRecord record) {
    synchronized (lock) {
      if (currentToast == record || nextToast == record) {
        cancelToastLocked(record, CustomToast.DISMISS_EVENT_TIMEOUT);
      }
    }
  }
}
