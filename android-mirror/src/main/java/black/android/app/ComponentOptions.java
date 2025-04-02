package black.android.app;

import java.lang.reflect.Field;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BStaticField;

/**
 * @author virtual_space
 * @function
 **/
@BClassName("android.app.ComponentOptions")
public interface ComponentOptions {
    @BStaticField
    String KEY_PENDING_INTENT_BACKGROUND_ACTIVITY_ALLOWED();

    @BStaticField
    String KEY_PENDING_INTENT_BACKGROUND_ACTIVITY_ALLOWED_BY_PERMISSION();
}
