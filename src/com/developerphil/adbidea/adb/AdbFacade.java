package com.developerphil.adbidea.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pbreault on 10/6/13.
 */
public class AdbFacade {

    public static void uninstall(Project project) {
        executeOnFirstDevice(project, new AdbRunnable() {
            @Override
            public void run(Project project, IDevice device, AndroidFacet facet, String packageName) {
                try {
                    device.uninstallPackage(packageName);
                    info(String.format("<b>%s</b> uninstalled on %s", packageName, device.getName()));
                } catch (InstallException e1) {
                    error("Uninstall fail... " + e1.getMessage());
                    e1.printStackTrace();
                }
            }
        });
    }

    public static void kill(Project project) {
        executeOnFirstDevice(project, new AdbRunnable() {
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
        });
    }


    public static void startDefaultActivity(Project project) {
        executeOnFirstDevice(project, new AdbRunnable() {
            @Override
            public void run(Project project, IDevice device, AndroidFacet facet, String packageName) {
                String defaultActivityName = AndroidUtils.getDefaultActivityName(facet.getManifest());
                String component = packageName + "/" + defaultActivityName;

                try {
                    device.executeShellCommand("am start " + component, new GenericReceiver(), 5L, TimeUnit.MINUTES);
                    info(String.format("<b>%s</b> started app on %s", packageName, device.getName()));
                } catch (Exception e1) {
                    error("Start fail... " + e1.getMessage());
                    e1.printStackTrace();
                }
            }
        });
    }

    public static void restartDefaultActivity(Project project) {
        kill(project);
        startDefaultActivity(project);
    }

    private static void executeOnFirstDevice(Project project, AdbRunnable runnable) {
        List<AndroidFacet> facets = AndroidUtils.getApplicationFacets(project);
        if (!facets.isEmpty()) {
            AndroidFacet facet = facets.get(0);
            String packageName = facet.getManifest().getPackage().getXmlAttributeValue().getValue();

            AndroidDebugBridge bridge = facet.getDebugBridge();
            if (bridge.isConnected() && bridge.hasInitialDeviceList()) {
                IDevice[] devices = bridge.getDevices();
                if (devices.length > 0) {
                    IDevice device = devices[0];
                    runnable.run(project, device, facet, packageName);
                } else {
                    error("No Device found");
                }
            }
        }
    }

    private static interface AdbRunnable {
        void run(Project project, IDevice device, AndroidFacet facet, String packageName);
    }


    private static void info(String message) {
        sendNotification(message, NotificationType.INFORMATION);
    }

    private static void error(String message) {
        sendNotification(message, NotificationType.ERROR);
    }

    private static void sendNotification(String message, NotificationType notificationType) {
        Notification notification = new Notification("com.developerphil.adbidea", "Adb IDEA", message, notificationType);
        Notifications.Bus.notify(notification);
    }

}
