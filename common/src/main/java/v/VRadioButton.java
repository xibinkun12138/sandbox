package v;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatRadioButton;

public class VRadioButton extends AppCompatRadioButton {

  public VRadioButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs, defStyle);
  }

  public VRadioButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public VRadioButton(Context context) {
    super(context);
    init(context, null, 0);
  }

  private void init(Context context, AttributeSet attrs, int defStyle) {
    TTypeface.init(this, context, attrs, defStyle);
  }

  @Override
  public void setTextAppearance(Context context, int resid) {
    super.setTextAppearance(context, resid);
    TTypeface.setTextAppearance(this, context, resid);
  }
}
