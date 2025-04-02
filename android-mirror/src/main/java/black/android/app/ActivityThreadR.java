package black.android.app;

import android.app.ActivityThread.ActivityClientRecord;
import java.util.List;
import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

@BClassName("android.app.ActivityThread")
public interface ActivityThreadR {
  @BMethod
  void handleNewIntent(ActivityClientRecord record, List List1);
}
