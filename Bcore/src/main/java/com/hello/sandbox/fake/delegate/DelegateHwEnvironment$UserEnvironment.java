package com.hello.sandbox.fake.delegate;

import android.os.Environment$UserEnvironment;
import com.hello.sandbox.core.IOCore;
import java.io.File;

public class DelegateHwEnvironment$UserEnvironment  extends Environment$UserEnvironment {

  private static final String TAG = "DelegateHwEnvironment$UserEnvironment";
  private  Environment$UserEnvironment origin;

  public DelegateHwEnvironment$UserEnvironment(Object origin) {
    this.origin = (Environment$UserEnvironment) origin;
  }

  public File[] getExternalDirs() {
   return redirectPath(origin.getExternalDirs());
  }



  public File getExternalStorageDirectory() {
     return redirectPath(origin.getExternalStorageDirectory());
  }



  public File getExternalStoragePublicDirectory(String type) {
     return redirectPath(origin.getExternalStoragePublicDirectory(type));
  }

  public File[] buildExternalStoragePublicDirs(String type) {
     return redirectPath(origin.buildExternalStoragePublicDirs(type));
  }

  public File[] buildExternalStorageAndroidDataDirs() {
     return redirectPath(origin.buildExternalStorageAndroidDataDirs());
  }

  public File[] buildExternalStorageAndroidObbDirs() {
     return redirectPath(origin.buildExternalStorageAndroidObbDirs());
  }

  public File[] buildExternalStorageAppDataDirs(String packageName) {
     return redirectPath(origin.buildExternalStorageAppDataDirs(packageName));
  }

  public File[] buildExternalStorageAppMediaDirs(String packageName) {
     return origin.buildExternalStorageAppMediaDirs(packageName);
  }

  public File[] buildExternalStorageAppObbDirs(String packageName) {
     return redirectPath(origin.buildExternalStorageAppObbDirs(packageName));
  }

  public File[] buildExternalStorageAppFilesDirs(String packageName) {
     return redirectPath(origin.buildExternalStorageAppFilesDirs(packageName));
  }

  public File[] buildExternalStorageAppCacheDirs(String packageName) {
     return redirectPath(origin.buildExternalStorageAppCacheDirs(packageName));
  }

  private  File[] redirectPath(File[] files){
    File[] newFiles = new File[files.length];
    for (int i = 0; i< files.length;i++) {
      newFiles[i]=  IOCore.get().redirectPath(files[i]);
    }
    return newFiles;
  }
  private  File redirectPath(File file){
    return IOCore.get().redirectPath(file);
  }
}
