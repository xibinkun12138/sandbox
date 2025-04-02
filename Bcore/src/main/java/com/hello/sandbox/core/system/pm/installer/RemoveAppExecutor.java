package com.hello.sandbox.core.system.pm.installer;

import com.hello.sandbox.core.env.BEnvironment;
import com.hello.sandbox.core.system.pm.BPackageSettings;
import com.hello.sandbox.entity.pm.InstallOption;
import com.hello.sandbox.utils.FileUtils;

/** Created by Milk on 4/27/21. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug */
public class RemoveAppExecutor implements Executor {
  @Override
  public int exec(BPackageSettings ps, InstallOption option, int userId) {
    FileUtils.deleteDir(BEnvironment.getAppDir(ps.pkg.packageName));
    return 0;
  }
}
