package com.draekko.adbtools.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import static com.draekko.adbtools.ui.NotificationHelper.error;
import static com.draekko.adbtools.ui.NotificationHelper.info;

public class UninstallCommand implements Command {
    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            String errorCode = device.uninstallPackage(packageName);
            if (errorCode == null) {
                info(String.format("<b>%s</b> uninstalled on %s", packageName, device.getName()));
                return true;
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.getName()));
            }
        } catch (InstallException e1) {
            error("Uninstall fail... " + e1.getMessage());
        }
        return false;
    }
}
