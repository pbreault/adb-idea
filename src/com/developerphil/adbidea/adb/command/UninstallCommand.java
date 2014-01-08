package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

/**
 * Created by pbreault on 1/2/14.
 */
public class UninstallCommand implements Command {
    @Override
    public void run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            String errorCode = device.uninstallPackage(packageName);
            if (errorCode == null) {
                info(String.format("<b>%s</b> uninstalled on %s", packageName, device.getName()));
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.getName()));
            }
        } catch (InstallException e1) {
            error("Uninstall fail... " + e1.getMessage());
            e1.printStackTrace();
        }
    }
}
