package com.hello.sandbox.calc.frag;

import static android.content.Context.VIBRATOR_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.hello.sandbox.calc.R;
import com.hello.sandbox.calc.core.AutoCalc;
import com.hello.sandbox.calc.core.AutoCalcException;
import com.hello.sandbox.calc.util.AlertDialogTool;
import com.hello.sandbox.calc.util.ExceptionUtils;
import com.hello.sandbox.calc.util.PixelTool;
import com.hello.sandbox.calc.view.AutofitTextView;
import java.math.BigDecimal;

public class CalcFragment extends Fragment {

  private final int TEXT_MAX_LENGTH = 50;
  private final int PAD_MODE_NORMAL = 0;
  private final int PAD_MODE_SCIENCE = 1;
  private final int PAD_MODE_PROGRAMMER = 2;
  private final int BC_MODE_DEC = 0;

  //Consts
  private final int BC_MODE_BIN = 1;

  //Mode
  private final int BC_MODE_HEX = 2;
  private final int BC_MODE_OCT = 3;
  private CalcResultChangeListener calcResultChangeListener;
  private boolean inited = false;
  private String text_error;
  private String text_auto_bc_error;
  private String text_mode_expand;

  //
  // ========================================
  private String text_mode_programmer;
  private Resources resources;
  private CharSequence fullMainText = "";
  private CharSequence fullMainResultText = "";
  private View root;
  private Context context;
  private Vibrator vibrator;
  private AutoCalc autoCalc;
  private LinearLayout layout_root;
  private ScrollView layout_top;
  private LinearLayout layout_history;
  private LinearLayout layout_binary_conversion;
  private LinearLayout [] pad_rows;
  private Button btn_pad_ac;
  private Button btn_pad_dot;
  private Button btn_pad_sqrt;
  private Button btn_pad_number_2;
  private Button btn_pad_number_3;
  private Button btn_pad_number_4;
  private Button btn_pad_number_5;
  private Button btn_pad_number_6;
  private Button btn_pad_number_7;
  private Button btn_pad_number_8;
  private Button btn_pad_number_9;
  private Button btn_pad_number_A;
  private Button btn_pad_number_B;
  private Button btn_pad_number_C;
  private Button btn_pad_number_D;
  private Button btn_pad_number_E;
  private Button btn_pad_number_F;
  private Button btn_sin;
  private Button btn_2nd;
  private Button btn_cos;
  private Button btn_tan;
  private Button btn_deg_rad;
  private AutofitTextView text_main;
  private AutofitTextView text_main_pre_result;
  private TextView text_oct;
  private TextView text_bin;
  private TextView text_hex;
  private TextView text_dec;
  private RadioButton radio_binary_conversion_hex;
  private RadioButton radio_binary_conversion_bin;
  private RadioButton radio_binary_conversion_oct;
  private RadioButton radio_binary_conversion_dec;
  private StringBuilder textBuffer = new StringBuilder("0");
  private boolean isCalced = false;
  private boolean isCalcAndError = false;
  private OnPadModeChangedListener onPadModeChangedListener;
  private int calcHistoryMaxCount = 0;
  private int calcScale = 8;
  private int scientificNotationMax = 100000;
  private boolean autoScientificNotation = true;
  private int padMode = PAD_MODE_NORMAL;

  //Text
  //========================================
  private int bcMode = BC_MODE_DEC;
  private boolean is2rnd = false;
  private boolean recordStep = false;
  private boolean isdeg = true;

  //switch modes
  //========================================
  private boolean useTouchVibrator = true;
  private boolean autoCheckUpdate = true;

  //Text input control
  //========================================

  public static CalcFragment newInstance(){
    return new CalcFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.calc_main, null);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    context = getContext();
    root = view;

    loadSettings();
    initAllControls();

    initResources();
    initCalc();

    vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

    layout_top.setVerticalFadingEdgeEnabled(true);
    layout_top.setFadingEdgeLength(50);

    layout_root.post(() -> {
      initControls();
      initLayout();

      update2rnd();
      updateDegRad();
      updateBinaryConversionMode();
      updateCalcSettings();
      layout_top.postDelayed(() -> {
        layout_top.fullScroll(ScrollView.FOCUS_DOWN);
        inited = true;
      }, 1050);
    });
  }

  @Override
  public void onDestroyView() {
    saveSettings();
    super.onDestroyView();
  }

  //update ui
  //=====================================

  private void initAllControls() {

    layout_root = root.findViewById(R.id.layout_root);
    layout_top = root.findViewById(R.id.layout_top);

    layout_binary_conversion = root.findViewById(R.id.layout_binary_conversion);
    layout_history = root.findViewById(R.id.layout_history);
    text_main = root.findViewById(R.id.text_main);
    text_main_pre_result = root.findViewById(R.id.text_main_pre_result);

    text_oct = root.findViewById(R.id.text_oct);
    text_bin = root.findViewById(R.id.text_bin);
    text_hex = root.findViewById(R.id.text_hex);
    text_dec = root.findViewById(R.id.text_dec);

    radio_binary_conversion_hex = root.findViewById(R.id.radio_binary_conversion_hex);
    radio_binary_conversion_bin = root.findViewById(R.id.radio_binary_conversion_bin);
    radio_binary_conversion_oct = root.findViewById(R.id.radio_binary_conversion_oct);
    radio_binary_conversion_dec = root.findViewById(R.id.radio_binary_conversion_dec);

    btn_deg_rad = root.findViewById(R.id.btn_deg_rad);
    btn_pad_dot = root.findViewById(R.id.btn_pad_dot);
    btn_pad_sqrt = root.findViewById(R.id.btn_sqrt);
    btn_pad_ac = root.findViewById(R.id.btn_pad_ac);
    btn_sin = root.findViewById(R.id.btn_sin);
    btn_cos = root.findViewById(R.id.btn_cos);
    btn_tan = root.findViewById(R.id.btn_tan);
    btn_2nd = root.findViewById(R.id.btn_2nd);

    pad_rows = new LinearLayout[9];
    pad_rows[0] = root.findViewById(R.id.layout_row_1);
    pad_rows[1] = root.findViewById(R.id.layout_row_2);
    pad_rows[2] = root.findViewById(R.id.layout_row_3);
    pad_rows[3] = root.findViewById(R.id.layout_row_4);
    pad_rows[4] = root.findViewById(R.id.layout_row_5);
    pad_rows[5] = root.findViewById(R.id.layout_row_6);
    pad_rows[6] = root.findViewById(R.id.layout_row_7);
    pad_rows[7] = root.findViewById(R.id.layout_row_1_programmer);
    pad_rows[8] = root.findViewById(R.id.layout_row_2_programmer);

    btn_pad_dot = root.findViewById(R.id.btn_pad_dot);
    btn_pad_number_2 = root.findViewById(R.id.btn_pad_number_2);
    btn_pad_number_3 = root.findViewById(R.id.btn_pad_number_3);
    btn_pad_number_4 = root.findViewById(R.id.btn_pad_number_4);
    btn_pad_number_5 = root.findViewById(R.id.btn_pad_number_5);
    btn_pad_number_6 = root.findViewById(R.id.btn_pad_number_6);
    btn_pad_number_7 = root.findViewById(R.id.btn_pad_number_7);
    btn_pad_number_8 = root.findViewById(R.id.btn_pad_number_8);
    btn_pad_number_9 = root.findViewById(R.id.btn_pad_number_9);

    btn_pad_number_A = root.findViewById(R.id.btn_pad_number_A);
    btn_pad_number_B = root.findViewById(R.id.btn_pad_number_B);
    btn_pad_number_C = root.findViewById(R.id.btn_pad_number_C);
    btn_pad_number_D = root.findViewById(R.id.btn_pad_number_D);
    btn_pad_number_E = root.findViewById(R.id.btn_pad_number_E);
    btn_pad_number_F = root.findViewById(R.id.btn_pad_number_F);
  }

  private void initControls() {
    final AnimationDrawable frame_animation_btn_switch = (AnimationDrawable)resources.getDrawable(R.drawable.frame_animation_btn_switch, null);
    final Drawable btn_pad_switch = resources.getDrawable(R.drawable.btn_pad_switch, null);

    Button btn_pad_expand_collapse = root.findViewById(R.id.btn_pad_expand_collapse);
    btn_pad_expand_collapse.setOnClickListener(v -> {

      if(padMode == PAD_MODE_NORMAL) setPadMode(PAD_MODE_SCIENCE);
      else if(padMode == PAD_MODE_SCIENCE) setPadMode(PAD_MODE_NORMAL);
      else if(padMode == PAD_MODE_PROGRAMMER) setPadMode(PAD_MODE_NORMAL);

      initLayout();
      clearText();
      setBinaryConversionMode(BC_MODE_DEC);

     // btn_pad_expand_collapse.setForeground(frame_animation_btn_switch);
      frame_animation_btn_switch.start();
      btn_pad_expand_collapse.postDelayed(() -> {
        frame_animation_btn_switch.stop();
       // btn_pad_expand_collapse.setForeground(btn_pad_switch);
      }, 2000);
    });

    text_main_pre_result.addTextChangedListener(new TextWatcher() {
      private boolean autoSetting = false;
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length() > TEXT_MAX_LENGTH * 2 && !autoSetting) {
          autoSetting = true;
          fullMainResultText = s;
          text_main_pre_result.setText(resources.getString(R.string.text_text_too_long));
          autoSetting = false;
        }
      }
      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    text_main.addTextChangedListener(new TextWatcher() {
      private boolean autoSetting = false;
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(s.length() > TEXT_MAX_LENGTH * 2 && !autoSetting) {
          autoSetting = true;
          fullMainText = s;
          text_main.setText(resources.getString(R.string.text_text_too_long));
          autoSetting = false;
        }
      }
      @Override
      public void afterTextChanged(Editable s) {
        if (s==null){
          return;
        }
        if (  calcResultChangeListener!=null) {
          calcResultChangeListener.onChange(s.toString());
        }
      }
    });

    radio_binary_conversion_hex.setOnClickListener((v) -> setBinaryConversionMode(BC_MODE_HEX));
    radio_binary_conversion_bin.setOnClickListener((v) -> setBinaryConversionMode(BC_MODE_BIN));
    radio_binary_conversion_oct.setOnClickListener((v) -> setBinaryConversionMode(BC_MODE_OCT));
    radio_binary_conversion_dec.setOnClickListener((v) -> setBinaryConversionMode(BC_MODE_DEC));

    root.findViewById(R.id.btn_pad_number_0).setOnClickListener(v -> writeText("0", false, false));
    root.findViewById(R.id.btn_pad_number_1).setOnClickListener(v -> writeText("1", false, false));
    root.findViewById(R.id.btn_pad_number_2).setOnClickListener(v -> writeText("2", false, false));
    root.findViewById(R.id.btn_pad_number_3).setOnClickListener(v -> writeText("3", false, false));
    root.findViewById(R.id.btn_pad_number_4).setOnClickListener(v -> writeText("4", false, false));
    root.findViewById(R.id.btn_pad_number_5).setOnClickListener(v -> writeText("5", false, false));
    root.findViewById(R.id.btn_pad_number_6).setOnClickListener(v -> writeText("6", false, false));
    root.findViewById(R.id.btn_pad_number_7).setOnClickListener(v -> writeText("7", false, false));
    root.findViewById(R.id.btn_pad_number_8).setOnClickListener(v -> writeText("8", false, false));
    root.findViewById(R.id.btn_pad_number_9).setOnClickListener(v -> writeText("9", false, false));
    root.findViewById(R.id.btn_pad_number_A).setOnClickListener(v -> writeText("A", false, false));
    root.findViewById(R.id.btn_pad_number_B).setOnClickListener(v -> writeText("B", false, false));
    root.findViewById(R.id.btn_pad_number_C).setOnClickListener(v -> writeText("C", false, false));
    root.findViewById(R.id.btn_pad_number_D).setOnClickListener(v -> writeText("D", false, false));
    root.findViewById(R.id.btn_pad_number_E).setOnClickListener(v -> writeText("E", false, false));
    root.findViewById(R.id.btn_pad_number_F).setOnClickListener(v -> writeText("F", false, false));

    root.findViewById(R.id.btn_and).setOnClickListener(v -> writeText(" and ", true, false));
    root.findViewById(R.id.btn_or).setOnClickListener(v -> writeText(" or ", true, false));
    root.findViewById(R.id.btn_xor).setOnClickListener(v -> writeText(" xor ", true, false));
    root.findViewById(R.id.btn_not).setOnClickListener(v -> writeText(" not ", true, true));

    root.findViewById(R.id.btn_negate).setOnClickListener(v -> writeText("negate(", true, true));

    root.findViewById(R.id.btn_left_shift).setOnClickListener(v -> writeText(" << ", true, false));
    root.findViewById(R.id.btn_right_shift).setOnClickListener(v -> writeText(" >> ", true, false));
    root.findViewById(R.id.btn_right_shift_unsigned).setOnClickListener(v -> writeText(" >>> ", true, false));

    root.findViewById(R.id.btn_rpc).setOnClickListener(v -> writeText("1/", true, true));
    root.findViewById(R.id.btn_pad_del).setOnClickListener(v -> delText());
    root.findViewById(R.id.btn_pad_minus).setOnClickListener(v -> writeText("-", true, false));
    root.findViewById(R.id.btn_pad_div).setOnClickListener(v -> writeText("÷", true, false));
    root.findViewById(R.id.btn_pad_mul).setOnClickListener(v -> writeText("×", true, false));
    root.findViewById(R.id.btn_pad_plus).setOnClickListener(v -> writeText("+", true, false));
    root.findViewById(R.id.btn_pad_percent).setOnClickListener(v -> writeText(padMode == PAD_MODE_PROGRAMMER ? " mod " : "%", true, false));


    root.findViewById(R.id.btn_fac).setOnClickListener(v -> writeText("!", true, false));
    root.findViewById(R.id.btn_pi).setOnClickListener(v -> writeText("π", false, false));
    root.findViewById(R.id.btn_e).setOnClickListener(v -> writeText("е", false, false));
    root.findViewById(R.id.btn_pow).setOnClickListener(v -> writeText("^", true, false));
    root.findViewById(R.id.btn_lg).setOnClickListener(v -> writeText("lg(", true, true));
    root.findViewById(R.id.btn_ln).setOnClickListener(v -> writeText("ln(", true, true));
    root.findViewById(R.id.btn_left_p).setOnClickListener(v -> writeText("(", true, true));
    root.findViewById(R.id.btn_right_p).setOnClickListener(v -> writeText(")", false, false));
    root.findViewById(R.id.btn_left_p_2).setOnClickListener(v -> writeText("(", true, true));
    root.findViewById(R.id.btn_right_p_2).setOnClickListener(v -> writeText(")", false, false));

    btn_pad_sqrt.setOnClickListener(v -> writeText(is2rnd ? "∛" : "√", true, true));
    btn_2nd.setOnClickListener(v -> switch2rnd());
    btn_pad_ac.setOnClickListener(v -> clearTextOrLog());
    btn_deg_rad.setOnClickListener(v -> switchDegRad());

    btn_sin.setOnClickListener(v -> { if(is2rnd) writeText("arcsin(", true, true); else writeText("sin(", true, true); });
    btn_cos.setOnClickListener(v -> { if(is2rnd) writeText("arccos(", true, true); else writeText("cos(", true, true); });
    btn_tan.setOnClickListener(v -> { if(is2rnd) writeText("arctan(", true, true); else writeText("tan(", true, true); });
    btn_pad_dot.setOnClickListener(v -> writeText(is2rnd ? "," : ".", false, false));

    root.findViewById(R.id.btn_pad_equal).setOnClickListener(v -> { vibratorVibrate(); doCalc(); });
  }

  private void initLayout() {

    layout_root.measure(0,0);
    int width = layout_root.getWidth();
    int height = layout_root.getHeight();

    int colCount = 4, rowCount = 5;

    if(padMode == PAD_MODE_SCIENCE || padMode == PAD_MODE_PROGRAMMER) {
      colCount = 5;
      rowCount = 7;
    }

    updateBinaryConversionMode();

    layout_binary_conversion.setVisibility(padMode == PAD_MODE_PROGRAMMER ? View.VISIBLE : View.GONE);

    pad_rows[7].setVisibility(padMode == PAD_MODE_PROGRAMMER ? View.VISIBLE : View.GONE);
    pad_rows[8].setVisibility(padMode == PAD_MODE_PROGRAMMER ? View.VISIBLE : View.GONE);

    pad_rows[0].setVisibility(padMode == PAD_MODE_SCIENCE ? View.VISIBLE : View.GONE);
    pad_rows[1].setVisibility(padMode == PAD_MODE_SCIENCE ? View.VISIBLE : View.GONE);

    btn_pad_dot.setVisibility(padMode == PAD_MODE_PROGRAMMER ? View.GONE : View.VISIBLE);
    btn_pad_number_E.setVisibility(padMode == PAD_MODE_PROGRAMMER ? View.VISIBLE : View.GONE);
    btn_pad_number_F.setVisibility(padMode == PAD_MODE_PROGRAMMER ? View.VISIBLE : View.GONE);

    int margin = padMode == PAD_MODE_SCIENCE || padMode == PAD_MODE_PROGRAMMER ? 20 : 26;
    int btnSize = width / colCount - margin * 2;

    for (int i = 0; i < pad_rows.length; i++) {
      LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) pad_rows[i].getLayoutParams();
      lp3.width = LinearLayout.LayoutParams.MATCH_PARENT;
      lp3.height = btnSize;
      pad_rows[i].setLayoutParams(lp3);
      for (int j = 0; j < pad_rows[i].getChildCount(); j++) {
        View b = pad_rows[i].getChildAt(j);
        if (j == 0 && i < 7)
          b.setVisibility(padMode == PAD_MODE_SCIENCE ? View.VISIBLE : View.GONE);
        else if (j == 1 && i >= 2 && i < 6)
          b.setVisibility(padMode == PAD_MODE_PROGRAMMER ? View.VISIBLE : View.GONE);

        lp3 = (LinearLayout.LayoutParams) b.getLayoutParams();
        lp3.width = btnSize;
        lp3.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp3.setMargins(margin, 0, margin, 0);

        b.setLayoutParams(lp3);
      }
    }

    ViewGroup.LayoutParams lp = layout_top.getLayoutParams();
    lp.height = (height - rowCount * btnSize);
    layout_top.setLayoutParams(lp);
    layout_history.setMinimumHeight(lp.height - PixelTool.dpToPx(context, 80));

    layout_root.requestLayout();

    //Log.d(TAG, "padWidth = " + padWidth + ",padHeight = " + padHeight);
    //Log.d(TAG, "btnWidth = " + btnWidth + ",btnHeight = " + btnHeight);
  }

  private void initResources() {
    resources = this.getResources();
    text_error = resources.getString(R.string.text_error);
    text_auto_bc_error = (String) resources.getText(R.string.text_auto_bc_error);
    text_mode_expand = resources.getString(R.string.text_mode_expand);
    text_mode_programmer = resources.getString(R.string.text_mode_programmer);
  }

  private void initCalc() {
    autoCalc = new AutoCalc();
    //debug functions
    autoCalc.addCalcFunctionActuatorSolver("test2", 0, false, false,
            (formula, ulaBuffer, functionName, adians, params) -> {
              return "";
            }, "");
  }

  private void vibratorVibrate() {
    if(inited && useTouchVibrator) vibrator.vibrate(30);
  }

  private void switch2rnd() {
    is2rnd = !is2rnd;
    vibratorVibrate();
    update2rnd();
  }

  private void switchDegRad() {
    isdeg = !isdeg;
    vibratorVibrate();
    updateDegRad();

    //提示
    if(isdeg) Toast.makeText(context, resources.getText(R.string.text_use_deg), Toast.LENGTH_SHORT).show();
    else Toast.makeText(context, resources.getText(R.string.text_use_rad), Toast.LENGTH_SHORT).show();
  }

  private void clearText() {
    isCalcAndError = false;
    textBuffer = new StringBuilder("0");
    writeText("0", false, false);
  }

  private void clearTextOrLog() {
    if(!textBuffer.toString().equals("0"))
     clearText();
  }

  private void writeText(String s, boolean isOp, boolean isLeft) {
    vibratorVibrate();
    if((textBuffer.toString().equals("0") && !s.equals("."))
            || isCalcAndError
            || (isCalced && !isOp)) textBuffer = new StringBuilder(s);
    else if(textBuffer.length() < TEXT_MAX_LENGTH) {
      if(isLeft && autoCalc.testIsNumber(textBuffer.toString())) {
        textBuffer.insert(0, s);
        if(s.endsWith("("))
          textBuffer.append( ")");
      }
      else textBuffer.append(s);
    }
    isCalcAndError = false;
    isCalced = false;
    updateText();
  }

  private void delText() {
    vibratorVibrate();
    if(textBuffer.length() > 0)
      textBuffer.deleteCharAt(textBuffer.length() - 1);
    if(textBuffer.length() == 0)
      textBuffer.append("0");
    updateText();
  }

  private void updateText() {

    if(textBuffer.toString().equals("0"))
      btn_pad_ac.setForeground(resources.getDrawable(R.drawable.btn_pad_ac, null));
    else
      btn_pad_ac.setForeground(resources.getDrawable(R.drawable.btn_pad_c, null));

    preCalc();

    text_main.setText(textBuffer.toString());

    if(padMode == PAD_MODE_PROGRAMMER)
      updateBinaryConversionTexts();

    layout_top.postDelayed(() -> layout_top.fullScroll(ScrollView.FOCUS_DOWN), 300);
  }

  private void update2rnd() {
    if(is2rnd) {
      btn_sin.setForeground(resources.getDrawable(R.drawable.arcsin, null));
      btn_cos.setForeground(resources.getDrawable(R.drawable.arccos, null));
      btn_tan.setForeground(resources.getDrawable(R.drawable.arctan, null));
      btn_pad_dot.setForeground(resources.getDrawable(R.drawable.comma, null));
      btn_pad_sqrt.setForeground(resources.getDrawable(R.drawable.btn_cbrt, null));
      btn_2nd.setForeground(resources.getDrawable(R.drawable.btn_2nd_active, null));
    }else{
      btn_sin.setForeground(resources.getDrawable(R.drawable.sin, null));
      btn_cos.setForeground(resources.getDrawable(R.drawable.cos, null));
      btn_tan.setForeground(resources.getDrawable(R.drawable.tan, null));
      btn_pad_dot.setForeground(resources.getDrawable(R.drawable.btn_pad_dot, null));
      btn_pad_sqrt.setForeground(resources.getDrawable(R.drawable.btn_sqrt, null));
      btn_2nd.setForeground(resources.getDrawable(R.drawable.btn_2nd, null));
    }
  }


  //show dialogs
  //========================================

  private void updateDegRad() {
    autoCalc.setUseDegree(isdeg);
    if(isdeg)
      btn_deg_rad.setForeground(resources.getDrawable(R.drawable.deg, null));
    else
      btn_deg_rad.setForeground(resources.getDrawable(R.drawable.rad, null));
  }

  private void updateBinaryConversionTexts() {
    String text = textBuffer.toString();
    if(autoCalc.testIsNumber(text)) {
      try {
        BigDecimal n = autoCalc.getTools().strToNumber(text);
        if (autoCalc.getTools().checkNumberRange(n, "long")) {
          text_bin.setText(Long.toBinaryString(n.longValue()));
          text_oct.setText(Long.toOctalString(n.longValue()));
          text_hex.setText(Long.toHexString(n.longValue()));
          text_dec.setText(String.valueOf(n.longValue()));
        } else {
          text_bin.setText(text_auto_bc_error);
          text_oct.setText(text_auto_bc_error);
          text_hex.setText(text_auto_bc_error);
          text_dec.setText(text_auto_bc_error);
        }
      }catch (Exception e) {
        text_bin.setText(text_auto_bc_error);
        text_oct.setText(text_auto_bc_error);
        text_hex.setText(text_auto_bc_error);
        text_dec.setText(text_auto_bc_error);
      }
    }else {
      text_bin.setText("");
      text_oct.setText("");
      text_hex.setText("");
      text_dec.setText("");
    }
  }



  //设置
  //========================================

  private void updateBinaryConversionMode() {
    if(padMode == PAD_MODE_PROGRAMMER) {

      clearText();
      autoCalc.setBcMode(bcMode);

      radio_binary_conversion_bin.setChecked(false);
      radio_binary_conversion_oct.setChecked(false);
      radio_binary_conversion_dec.setChecked(false);
      radio_binary_conversion_hex.setChecked(false);

      btn_pad_number_2.setEnabled(bcMode != BC_MODE_BIN);
      btn_pad_number_3.setEnabled(bcMode != BC_MODE_BIN);
      btn_pad_number_4.setEnabled(bcMode != BC_MODE_BIN);
      btn_pad_number_5.setEnabled(bcMode != BC_MODE_BIN);
      btn_pad_number_6.setEnabled(bcMode != BC_MODE_BIN);
      btn_pad_number_7.setEnabled(bcMode != BC_MODE_BIN);
      btn_pad_number_8.setEnabled(bcMode != BC_MODE_BIN && bcMode != BC_MODE_OCT);
      btn_pad_number_9.setEnabled(bcMode != BC_MODE_BIN && bcMode != BC_MODE_OCT);

      btn_pad_number_A.setEnabled(bcMode == BC_MODE_HEX);
      btn_pad_number_B.setEnabled(bcMode == BC_MODE_HEX);
      btn_pad_number_C.setEnabled(bcMode == BC_MODE_HEX);
      btn_pad_number_D.setEnabled(bcMode == BC_MODE_HEX);
      btn_pad_number_E.setEnabled(bcMode == BC_MODE_HEX);
      btn_pad_number_F.setEnabled(bcMode == BC_MODE_HEX);

      switch (bcMode) {
        case BC_MODE_BIN:
          radio_binary_conversion_bin.setChecked(true);
          break;
        case BC_MODE_OCT:
          radio_binary_conversion_oct.setChecked(true);
          break;
        case BC_MODE_DEC:
          radio_binary_conversion_dec.setChecked(true);
          break;
        case BC_MODE_HEX:
          radio_binary_conversion_hex.setChecked(true);
          break;
      }
    }
    else {
      btn_pad_number_2.setEnabled(true);
      btn_pad_number_3.setEnabled(true);
      btn_pad_number_4.setEnabled(true);
      btn_pad_number_5.setEnabled(true);
      btn_pad_number_6.setEnabled(true);
      btn_pad_number_7.setEnabled(true);
      btn_pad_number_8.setEnabled(true);
      btn_pad_number_9.setEnabled(true);
    }
  }

  private void updateCalcSettings() {
    autoCalc.setRecordStep(recordStep);
    autoCalc.setNumberScale(calcScale);
    autoCalc.setUseDegree(isdeg);
    autoCalc.setAutoScientificNotation(autoScientificNotation);
    autoCalc.setScientificNotationMax(scientificNotationMax);
  }

  private void setBinaryConversionMode(int mode) {
    bcMode = mode;
    autoCalc.setBcMode(bcMode);
    updateBinaryConversionMode();
  }

  private void setPadMode(int mode) {
    padMode = mode;
    initLayout();
    if(onPadModeChangedListener != null) {
      switch (mode) {
        case PAD_MODE_NORMAL:onPadModeChangedListener.onPadModeChanged("");break;
        case PAD_MODE_SCIENCE:onPadModeChangedListener.onPadModeChanged(text_mode_expand);break;
        case PAD_MODE_PROGRAMMER:onPadModeChangedListener.onPadModeChanged(text_mode_programmer);break;
      }
    }
  }

  public void setOnPadModeChangedListener(OnPadModeChangedListener listener) {
    onPadModeChangedListener = listener;
  }

  private String preSolveParentheses(boolean isPre) {
    StringBuilder textBufferNew = isPre ? new StringBuilder(textBuffer) : textBuffer;
    String formula = textBufferNew.toString();
    int leftPos = formula.indexOf('(');
    if(leftPos >= 0 && leftPos < formula.length() - 1 && !formula.contains(")")) {
      String formulaRight = formula.substring(leftPos + 1);
      if(autoCalc.testIsNumber(formulaRight)) {
        textBufferNew.append(')');
        formula = textBufferNew.toString();
      }
    }
    return formula;
  }

  private void preCalc() {
    String formula = preSolveParentheses(true);
    if(!formula.equals("") && !formula.equals("0")) {
      if (!(autoCalc.isOperator(formula.charAt(formula.length() - 1), AutoCalc.OP_TYPE_BOTH)
              || autoCalc.isOperator(formula.charAt(formula.length() - 1), AutoCalc.OP_TYPE_START))
              && formula.charAt(formula.length() - 1) != '(') {
        text_main_pre_result.setVisibility(View.VISIBLE);
        String result = autoCalc.calc(formula);
        StringBuilder sb = new StringBuilder("=");

        if (autoCalc.isLastSuccess()) sb.append(result);
        else {
          if (!autoCalc.isOperatorOrParentheses(formula.charAt(formula.length() - 1))
                  && formula.length() >= 3) sb.append(text_error);
          else sb.deleteCharAt(0);
        }

        text_main_pre_result.setText(sb.toString());
        autoCalc.resetLastSuccess();
      }else text_main_pre_result.setText("");
    }else text_main_pre_result.setVisibility(View.GONE);
  }

  private void doCalc() {
    String result;
    String formula = preSolveParentheses(false);

    if(formula.equals("0")) return;
    if(isCalcAndError) return;

    result = autoCalc.calc(formula);


    textBuffer = new StringBuilder(result);
    text_main_pre_result.setVisibility(View.GONE);
    text_main.setText(result);
    updateBinaryConversionTexts();

    isCalcAndError = !autoCalc.isLastSuccess();
    isCalced = true;

    if(isCalcAndError) {
      Exception e = autoCalc.getLastException();
      if(e != null && !(e instanceof AutoCalcException))
        showException(e);
    }
  }

  public void chooseMode() {

    LayoutInflater inflater = LayoutInflater.from(context);
    View v = inflater.inflate(R.layout.dialog_choose_mode, null);

    AlertDialog dialog = AlertDialogTool.buildCustomStylePopupDialogGravity(context, v, Gravity.TOP, R.style.DialogTopPopup);

    Button btn_close = v.findViewById(R.id.btn_cancel);
    Button btn_mode_normal = v.findViewById(R.id.btn_mode_normal);
    Button btn_mode_expanded = v.findViewById(R.id.btn_mode_expanded);
    Button btn_mode_programmer = v.findViewById(R.id.btn_mode_programmer);;


    switch (padMode) {
      case PAD_MODE_NORMAL:
        btn_mode_normal.setBackgroundResource(R.drawable.btn_calculate);
        btn_mode_normal.setTextColor(Color.WHITE);
        break;
      case PAD_MODE_SCIENCE:
        btn_mode_expanded.setBackgroundResource(R.drawable.btn_calculate);
        btn_mode_expanded.setTextColor(Color.WHITE);
        break;
      case PAD_MODE_PROGRAMMER:
        btn_mode_programmer.setBackgroundResource(R.drawable.btn_calculate);
        btn_mode_programmer.setTextColor(Color.WHITE);
        break;
    }

    btn_close.setOnClickListener(view -> dialog.dismiss());
    btn_mode_normal.setOnClickListener(view -> {
      dialog.dismiss();
      setPadMode(PAD_MODE_NORMAL);
      setBinaryConversionMode(BC_MODE_DEC);
      initLayout();
    });
    btn_mode_expanded.setOnClickListener(view -> {
      dialog.dismiss();
      setPadMode(PAD_MODE_SCIENCE);
      setBinaryConversionMode(BC_MODE_DEC);
    });
    btn_mode_programmer.setOnClickListener(view -> {
      dialog.dismiss();
      setPadMode(PAD_MODE_PROGRAMMER);
    });

    dialog.show();
  }

  public void showException(Exception e) {

    LayoutInflater inflater = LayoutInflater.from(context);
    View v = inflater.inflate(R.layout.dialog_show_text, null);

    AlertDialog dialog = AlertDialogTool.buildCustomBottomPopupDialog(context, v);

    ((TextView)v.findViewById(R.id.text_title)).setText(R.string.title_calc_exception);
    ((TextView)v.findViewById(R.id.text_sub_title)).setText(e.getMessage());
    ((TextView)v.findViewById(R.id.text_main)).setText(ExceptionUtils.exceptionToString(e));
    v.findViewById(R.id.btn_ok).setOnClickListener(view -> dialog.dismiss());

    dialog.show();
  }

  private void loadSettings() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    recordStep = prefs.getBoolean("calc_record_step", false);
    padMode = prefs.getInt("calc_pad_expand", PAD_MODE_NORMAL);
    bcMode = prefs.getInt("calc_bc_mode", BC_MODE_DEC);
    is2rnd = prefs.getBoolean("calc_use_2rnd", false);
    isdeg = prefs.getBoolean("calc_use_deg", true);
    useTouchVibrator = prefs.getBoolean("calc_use_vibrator", true);
    calcHistoryMaxCount = prefs.getInt("calc_history_count", 20);
    calcScale = prefs.getInt("calc_computation_accuracy", 8);
    scientificNotationMax = prefs.getInt("calc_scientific_notation_max", 100000);
    autoScientificNotation = prefs.getBoolean("calc_auto_scientific_notation", true);
    autoCheckUpdate = prefs.getBoolean("calc_auto_update", false);
  }

  private void saveSettings() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = prefs.edit();

    editor.putInt("calc_pad_expand", padMode);
    editor.putInt("calc_bc_mode", bcMode);
    editor.putBoolean("calc_use_2rnd", is2rnd);
    editor.putBoolean("calc_use_deg", isdeg);
    editor.putInt("calc_history_count", calcHistoryMaxCount);

    editor.apply();
  }

  public void updateSettings() {
    loadSettings();
    updateCalcSettings();
  }

  public void setCalcResultChangeListener(
      CalcResultChangeListener calcResultChangeListener) {
    this.calcResultChangeListener = calcResultChangeListener;
  }

  public interface OnPadModeChangedListener {
    void onPadModeChanged(String newMdeString);
  }
}
