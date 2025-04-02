package v.pushbubble;

import android.app.Activity;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import com.hello.sandbox.common.R;
import com.hello.sandbox.common.util.Assert;
import com.hello.sandbox.common.util.Cu;
import com.hello.sandbox.common.util.LogUtils;
import com.hello.sandbox.common.util.NullChecker;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import rx.functions.Action0;
import v.VText;

/** 顶部气泡管理类，主要处理气泡间规避，在将气泡数据show的时候，如果当前有气泡展示，则 */
public class PushBubbleManager {

  private PushBubbleManager() {}

  private static final class SingleHolder {
    private static final PushBubbleManager instance = new PushBubbleManager();
  }

  public static PushBubbleManager getInstance() {
    return SingleHolder.instance;
  }

  public static final String TAG = "BubbleManager";

  private final Map<String, PriorityQueue<SequencePushBubbleInfo>> groups = new HashMap<>();

  private final Map<String, ShowLock> lockMap = new HashMap<>();

  /** @param actId 对用act的hashCode值 */
  public void clearAllBubble(int actId) {
    if (lockMap.size() > 0) {
      Iterator<Entry<String, ShowLock>> iterator = lockMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, ShowLock> next = iterator.next();
        if (next.getValue().getActId() == actId) iterator.remove();
      }
    }

    if (groups.size() > 0) {
      for (Iterator<Entry<String, PriorityQueue<SequencePushBubbleInfo>>> groupIt =
              groups.entrySet().iterator();
          groupIt.hasNext(); ) {
        Entry<String, PriorityQueue<SequencePushBubbleInfo>> next = groupIt.next();
        if (Cu.isEmpty(next.getValue())) {
          groupIt.remove();
          continue;
        }
        for (Iterator<SequencePushBubbleInfo> bubbleInfoIterator = next.getValue().iterator();
            bubbleInfoIterator.hasNext(); ) {
          SequencePushBubbleInfo sequenceBubbleInfo = bubbleInfoIterator.next();
          if (sequenceBubbleInfo.getActId() == actId) {
            bubbleInfoIterator.remove();
          }
        }
        if (Cu.isEmpty(next.getValue())) {
          groupIt.remove();
        }
      }
    }
  }

  /**
   * 展示气泡bubble，需要调气泡的地方，需要在dismiss的地方回调dismissCall，这样气泡才能显示下一个
   *
   * @param info 不排队的bubble
   */
  public void showSequenceBubble(@NonNull SequencePushBubbleInfo info) {
    showSequenceBubble(info, false);
  }

  private void showSequenceBubble(@NonNull SequencePushBubbleInfo info, boolean isNext) {
    Assert.isUiThread();
    PriorityQueue<SequencePushBubbleInfo> bubbleList = groups.get(info.getBubbleGroup());
    if (!Cu.isEmpty(bubbleList) && !isNext) {
      bubbleList.add(info);
      return;
    }
    if (lockMap.containsKey(info.getBubbleGroup())) {
      ShowLock showLock = lockMap.get(info.getBubbleGroup());
      if (showLock.isAllowShow()
          || (SystemClock.elapsedRealtime() - showLock.getCreateTime() >= showLock.getShowTime())) {
        int time = realShowBubble(info);
        if (time != 0) {
          showLock.setBubbleId(info.getBubbleId());
          showLock.setAllowShow(false);
          showLock.setCreateTime(SystemClock.elapsedRealtime());
          showLock.setShowTime(time);
        }
        return;
      }
      if (bubbleList == null) {
        bubbleList = new PriorityQueue<>();
        groups.put(info.getBubbleGroup(), bubbleList);
      }
      bubbleList.add(info);
      return;
    }
    int time = realShowBubble(info);
    ShowLock showLock =
        new ShowLock(
            info.getBubbleId(), SystemClock.elapsedRealtime(), false, time, info.getActId());
    lockMap.put(info.getBubbleGroup(), showLock);
  }

  private int realShowBubble(@NonNull SequencePushBubbleInfo info) {
    int time = info.showBubble();
    Action0 dismissAction = info.dismissCallBack;
    // 在气泡真正显示时修改气泡消失逻辑，让气泡能一个接一个显示下去
    info.setDismissCallBack(
        () -> {
          resolveDismiss(info, dismissAction);
        });
    return time;
  }

  /**
   * 气泡消失处理，气泡消失时移除在groups和lockMap中的记录，同时找寻下个显示的气泡
   *
   * @param info
   * @param dismissAction
   */
  private void resolveDismiss(@NonNull SequencePushBubbleInfo info, Action0 dismissAction) {
    LogUtils.d(TAG, "groups:" + groups.size() + " lockMap:" + lockMap.size());
    // 先将当前气泡的dismissAction调用了
    if (NullChecker.notNull(dismissAction)) {
      dismissAction.call();
    }
    // 移除groups中数据
    PriorityQueue<SequencePushBubbleInfo> infoList = groups.get(info.getBubbleGroup());
    if (!Cu.isEmpty(infoList)) {
      infoList.remove(info);
      // 该bubblegroup为空时，删除这条记录
      if (Cu.isEmpty(infoList)) {
        groups.remove(info.getBubbleGroup());
      }
    }
    // 移除lockMap中数据
    Iterator<ShowLock> iterator = lockMap.values().iterator();
    while (iterator.hasNext()) {
      ShowLock lock = iterator.next();
      if (TextUtils.equals(lock.getBubbleId(), info.getBubbleId())) {
        iterator.remove();
      }
    }
    // 展示下一个，优先选择当前组的下一个bubble
    SequencePushBubbleInfo nextBubble = !Cu.isEmpty(infoList) ? infoList.poll() : null;
    if (nextBubble == null && !groups.isEmpty()) {
      Collection<PriorityQueue<SequencePushBubbleInfo>> collection = groups.values();
      PriorityQueue<SequencePushBubbleInfo> infoLinkedList = collection.iterator().next();
      if (!Cu.isEmpty(infoLinkedList)) {
        nextBubble = infoLinkedList.poll();
      }
    }
    if (NullChecker.notNull(nextBubble)) {
      showSequenceBubble(nextBubble, true);
    }
  }

  public void showSimpleBubble(Activity act, String content) {
    VText contentView = (VText) LayoutInflater.from(act).inflate(R.layout.test_push_small_01, null);
    contentView.setText(content);
    SimplePushBubble bubble = new SimplePushBubble.Builder(act, contentView).build();
    PushBubbleManager.getInstance().showSequenceBubble(bubble);
  }
}
