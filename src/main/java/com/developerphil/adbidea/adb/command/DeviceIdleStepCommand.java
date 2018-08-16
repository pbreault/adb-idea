package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.intellij.openapi.project.Project;

import org.jetbrains.android.facet.AndroidFacet;

import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.adb.AdbUtil.isAppInstalled;
import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class DeviceIdleStepCommand implements Command {
    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            if (isAppInstalled(device, packageName)) {
                GenericReceiver receiver = new GenericReceiver();
                device.executeShellCommand("dumpsys deviceidle step", receiver, 15L, TimeUnit.SECONDS);
                receiver.getAdbOutputLines().stream().filter(s -> s.toLowerCase().contains("stepped")).forEach(s ->
                        info(String.format(s, packageName, device.getName())));
                return true;
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.getName()));
            }
        } catch (Exception e1) {
            error("dumpsys deviceidle step... " + e1.getMessage());
        }
        return false;
    }
}
