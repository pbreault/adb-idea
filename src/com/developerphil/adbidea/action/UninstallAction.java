package com.developerphil.adbidea.action;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.util.List;

/**
 * Created by pbreault on 9/28/13.
 */
public class UninstallAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(PlatformDataKeys.PROJECT);
        List<AndroidFacet> facets = AndroidUtils.getApplicationFacets(project);

        if (!facets.isEmpty()) {
            AndroidFacet facet = facets.get(0);
            String packageName = facet.getManifest().getPackage().getXmlAttributeValue().getValue();

            AndroidDebugBridge bridge = facet.getDebugBridge();
            if (bridge.isConnected() && bridge.hasInitialDeviceList()) {
                IDevice[] devices = bridge.getDevices();
                if (devices.length > 0) {
                    IDevice device = devices[0];

                    String deviceName = device.getName();

                    try {
                        device.uninstallPackage(packageName);
                        info(String.format("<b>%s</b> uninstalled on %s", packageName, deviceName));
                    } catch (InstallException e1) {
                        error("Uninstall fail... " + e1.getMessage());
                        e1.printStackTrace();
                    }

                    return;
                }
            }
        }
    }

    private void info(String message) {
        sendNotification(message, NotificationType.INFORMATION);
    }

    private void error(String message) {
        sendNotification(message, NotificationType.ERROR);
    }

    private void sendNotification(String message, NotificationType notificationType) {
        Notification notification = new Notification("com.developerphil.adbidea", "Adb IDEA", message, notificationType);
        Notifications.Bus.notify(notification);
    }

}
