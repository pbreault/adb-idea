package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.GenericReceiver;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

/**
* Created by pbreault on 1/2/14.
*/
public class KillCommand implements Command {
    @Override
    public void run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            device.executeShellCommand("am force-stop " + packageName, new GenericReceiver(), 5L, TimeUnit.MINUTES);
            info(String.format("<b>%s</b> forced-stop on %s", packageName, device.getName()));
        } catch (Exception e1) {
            error("Kill fail... " + e1.getMessage());
            e1.printStackTrace();
        }
    }
}
