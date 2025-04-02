package v.pushbubble;

import androidx.annotation.IntRange;
import rx.functions.Func0;

public abstract class SequencePushBubbleInfo extends PushBubbleInfo {

  /** 气泡展示前的判断条件 */
  protected Func0<Boolean> showCondition;

  public void setShowCondition(Func0<Boolean> showCondition) {
    this.showCondition = showCondition;
  }

  /**
   * 展示bubble
   *
   * @return 当前bubble展示时长，展示时长要大于等于0
   */
  public abstract @IntRange(from = 0) int showBubble();
}
