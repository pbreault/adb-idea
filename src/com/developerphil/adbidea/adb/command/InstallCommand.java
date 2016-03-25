package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;

import java.io.File;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class InstallCommand{
    public boolean run(IDevice device, File apk) {
        try {

            if (apk.exists()) {
                device.installPackage(apk.getAbsolutePath(), true);
            }

            info(String.format("<b>%s</b> installed on %s", apk.getName(), device.getName()));
            return true;
        } catch (InstallException e1) {
            error("Install fail... " + e1.getMessage());
        }
        return false;
    }
}
