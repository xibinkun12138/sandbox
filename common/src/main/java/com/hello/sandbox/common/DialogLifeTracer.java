package com.hello.sandbox.common;

public interface DialogLifeTracer {

  void onDialogAttachToWindow(android.app.Dialog dialog);

  void onDialogDetachFromWindow(android.app.Dialog dialog);
}
