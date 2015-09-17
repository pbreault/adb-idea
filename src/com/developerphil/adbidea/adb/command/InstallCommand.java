package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;

import java.io.File;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class InstallCommand{
    public boolean run(IDevice device, File apk) {
        try {

            String errorCode = null;
            if (apk.exists()) {
                errorCode = device.installPackage(apk.getAbsolutePath(), true, new String[]{});
            }

            if (errorCode == null) {
                info(String.format("<b>%s</b> installed on %s", apk.getName(), device.getName()));
                return true;
            } else {
                error(String.format("<b>%s</b> is not installed on %s\n%s", apk.getName(), device.getName(), errorCode));
            }
        } catch (InstallException e1) {
            error("Install fail... " + e1.getMessage());
        }
        return false;
    }
}
