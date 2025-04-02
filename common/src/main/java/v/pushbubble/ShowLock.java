package v.pushbubble;

public class ShowLock {
  private String bubbleId;
  private long createTime;
  private boolean allowShow = true;
  private int showTime;
  private int actId;

  public ShowLock(String bubbleId, long createTime, boolean allowShow, int showTime, int actId) {
    this.bubbleId = bubbleId;
    this.createTime = createTime;
    this.allowShow = allowShow;
    this.showTime = showTime;
    this.actId = actId;
  }

  public String getBubbleId() {
    return bubbleId;
  }

  public void setBubbleId(String bubbleId) {
    this.bubbleId = bubbleId;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public boolean isAllowShow() {
    return allowShow;
  }

  public void setAllowShow(boolean allowShow) {
    this.allowShow = allowShow;
  }

  public int getShowTime() {
    return showTime;
  }

  public void setShowTime(int showTime) {
    this.showTime = showTime;
  }

  public int getActId() {
    return actId;
  }

  public void setActId(int actId) {
    this.actId = actId;
  }
}
