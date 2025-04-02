package v;

import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import com.hello.sandbox.common.util.NullChecker;

public class VInputConnection extends BaseInputConnection {

  public VInputConnection(View targetView, boolean fullEditor) {
    super(targetView, fullEditor);
  }

  @Override
  public boolean commitText(CharSequence text, int newCursorPosition) {
    if (NullChecker.notNull(onCommitTextListener)) {
      return onCommitTextListener.commitText(text, newCursorPosition);
    }
    return true;
  }

  private OnCommitTextListener onCommitTextListener;

  public void setOnCommitTextListener(OnCommitTextListener onCommitTextListener) {
    this.onCommitTextListener = onCommitTextListener;
  }

  public interface OnCommitTextListener {
    boolean commitText(CharSequence text, int newCursorPosition);
  }
}
