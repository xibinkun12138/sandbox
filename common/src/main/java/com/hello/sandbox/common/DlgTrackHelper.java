package com.hello.sandbox.common;

public class DlgTrackHelper {

  public interface PermissionListener {
    void permissionShow();

    void allowClick();

    void denyClick(boolean noLongerAsk);
  }

  public interface RationaleListener {
    void rationaleShow();

    void allowClick();

    void denyClick();
  }

  public interface SettingListener {
    void settingDlgShow();

    void allowClick();

    void denyClick();
  }
}
