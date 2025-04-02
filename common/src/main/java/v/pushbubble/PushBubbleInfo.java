package v.pushbubble;

import rx.functions.Action0;

public abstract class PushBubbleInfo implements Comparable<PushBubbleInfo> {
  private long createTime; // 气泡即将去展示的创建时间
  protected Action0 showCallBack; // 如果气泡展示了，可以通过它回调通知
  protected Action0 dismissCallBack; // 气泡消失后会回调该方法

  /**
   * 用来标示bubble
   *
   * @return Bubble的ID
   */
  public abstract String getBubbleId();

  // 气泡的优先级，数值越大，优先级越低，目前有两个优先级，0跟10，默认0是高优先级
  protected int priority;

  public static final int LOW_PRIORITY = 10;

  public void setPriority(int priority) {
    this.priority = priority;
  }

  @Override
  public int compareTo(PushBubbleInfo o) {
    return priority - o.priority;
  }

  /**
   * 气泡有分组的概念：比如可以把一个Act，屏幕某一块区域所有气泡为一组；组与组之间的管理互不干扰。
   *
   * @return value 气泡归属于的组
   */
  public abstract String getBubbleGroup();

  /** @return value Act的hashCode值 */
  public abstract int getActId();

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public void setShowCallBack(Action0 showCallBack) {
    this.showCallBack = showCallBack;
  }

  public void setDismissCallBack(Action0 dismissCallBack) {
    this.dismissCallBack = dismissCallBack;
  }
}
