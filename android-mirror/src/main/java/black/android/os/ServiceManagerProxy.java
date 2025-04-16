package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

/**
 * @author virtual_space
 * @function
 **/
@BClassName("android.os.ServiceManager")
public interface ServiceManagerProxy {
    @BField
    IBinder mRemote();

    @BField
    IInterface mServiceManager();

}
