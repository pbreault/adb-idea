package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.intellij.openapi.project.Project;

import org.jetbrains.android.facet.AndroidFacet;

import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.adb.AdbUtil.isAppInstalled;
import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class ResetBatteryChargingCommand implements Command {
    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            if (isAppInstalled(device, packageName)) {
                device.executeShellCommand("dumpsys battery reset", new GenericReceiver(), 15L, TimeUnit.SECONDS);
                info(String.format("Reset battery charging on %s", device.getName()));
                return true;
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.getName()));
            }
        } catch (Exception e1) {
            error("dumpsys battery reset... " + e1.getMessage());
        }
        return false;
    }
}
