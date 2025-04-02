// IScreenOrientation.aidl
package top.niunaijun.blackboxa.screen;

import top.niunaijun.blackboxa.screen.ScreenFlippedListener;

interface IScreenOrientation {
    void registerScreenFlippedListener(in String packageName ,in ScreenFlippedListener listener);
    void open();
    void close();
}