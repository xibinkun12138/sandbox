package com.hello.sandbox.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.hello.sandbox.common.util.Vu;
import com.hello.sandbox.common.util.Assert;
import com.hello.sandbox.common.util.collections.Unit;
import java.lang.ref.WeakReference;
import rx.subjects.PublishSubject;

/** @author Aidan Follestad (afollestad) a lot extra layers */
public class DialogBase extends AlertDialog
    implements DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener,
        DialogChain.Chain,
        DialogLifecycleProvider {

  private WeakReference<Context> baseContext;
  private int priority = DialogChain.NORMAL;
  private long addTime;
  protected static final String POSITIVE = "POSITIVE";
  protected static final String NEGATIVE = "NEGATIVE";
  protected static final String NEUTRAL = "NEUTRAL";
  private OnShowListener showListener;
  private OnDismissListener dismissListener;
  private DialogLifeTracer dialogLifeTracer;

  private final PublishSubject<Unit> dialogSafe = PublishSubject.create();
  public final PublishSubject<Boolean> dialogDismiss = PublishSubject.create();

  protected DialogBase(Context context, boolean roundedbig, int theme) {
    super(
        context,
        theme == 0
            ? (roundedbig ? R.style.Theme_AppCompat_Light_Dialog_Alert_Roundedbig : 0)
            : theme);
    this.baseContext = new WeakReference<>(context);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    Activity act = Vu.getActivityFromContext(baseContext.get());
    if (act instanceof DialogLifeTracer) {
      tracer((DialogLifeTracer) act);
    }
  }

  protected void setViewInternal(View view) {
    //		setContentView(view);
    //		try {
    //			ViewGroup v = (ViewGroup) findViewById(android.R.id.content).getParent();
    //			if (v.getChildAt(0) instanceof TextView) {
    //				v.getChildAt(0).setVisibility(View.GONE);
    //			}
    //		} catch (Exception e) {
    //			if (Config.DEBUG) {
    //				throw e;
    //			} else {
    //				e.printStackTrace();
    //			}
    //		}
    setView(view);
  }

  @Override
  public final void setOnShowListener(OnShowListener listener) {
    showListener = listener;
  }

  @Override
  public final void setOnDismissListener(OnDismissListener listener) {
    dismissListener = listener;
  }

  protected final void setOnShowListenerInternal() {
    super.setOnShowListener(this);
  }

  protected final void setOnDismissListenerInternal() {
    super.setOnDismissListener(this);
  }

  @Override
  public void onShow(DialogInterface dialog) {
    dialogDismiss.onNext(false);
    if (showListener != null) showListener.onShow(dialog);
  }

  @Override
  public void dismiss() {
    if (priority == DialogChain.NORMAL) {
      super.dismiss();
      return;
    }

    Assert.isUiThread();
    if (isShowing()) {
      super.dismiss();
    } else {
      DialogChain.getInstance().removeCertainDialogFromQueue(baseContext.get(), this);
    }
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    if (dismissListener != null) dismissListener.onDismiss(dialog);
    dialogDismiss.onNext(true);
    if (priority == DialogChain.NORMAL) {
      return;
    }
    //    if (baseContext.get() instanceof DialogAct) {
    //      return; // dialog act 里的dialog 不再唤醒下一个节点
    //    }
    DialogChain.getInstance().showNext(baseContext.get(), true);
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (dialogLifeTracer != null) dialogLifeTracer.onDialogAttachToWindow(this);
  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (dialogLifeTracer != null) dialogLifeTracer.onDialogDetachFromWindow(this);
    dialogSafe.onNext(Unit.UNIT);
    dialogDismiss.onNext(false);
  }

  @Override
  public int getPriority() {
    return priority;
  }

  @Override
  public void showImmediate() {
    priority = DialogChain.IMMEDIATE;
    showWithQueue();
  }

  @Override
  public void showWithPriority(int priority) {
    if (priority <= 0) throw new IllegalArgumentException("priority should gt 0!!");
    this.priority = priority;
    showWithQueue();
  }

  private void showWithQueue() {
    //    if (baseContext.get() instanceof DialogAct) { // dialog act 里的dialog直接显示，不放入队列计算
    //      realShow();
    //      return;
    //    }
    addTime = System.currentTimeMillis();
    Assert.isUiThread();
    DialogChain.getInstance().addChain(baseContext.get(), this);
  }

  /** 如果存在dialog上需要再展示一个弹框，需要调用这个方法，不加入到队列管理 */
  @Override
  public void realShow() {
    try {
      super.show();
    } catch (WindowManager.BadTokenException e) {
      // 后续观察一下report的时候有没有其他问题
    }
  }

  @Override
  public long getAddTime() {
    return addTime;
  }

  @Override
  public void show() {
    realShow(); // 默认还是和以前一样,因为现在不确认项目中还有多少dialog上需要再展示一个弹框的case,如果要默认全部由queue管理，可以改成showWithQueue();
  }

  @Override
  public void tracer(@NonNull DialogLifeTracer tracer) {
    this.dialogLifeTracer = tracer;
  }
}
