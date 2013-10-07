package com.developerphil.adbidea.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.MultiLineReceiver;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pbreault on 10/6/13.
 */
public class AdbFacade {

    public static void uninstall(Project project) {
        executeOnFirstDevice(project, new AdbRunnable() {
            @Override
            public void run(Project project, IDevice device, String packageName) {
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
            public void run(Project project, IDevice device, String packageName) {
                try {
                    device.executeShellCommand("am force-stop " + packageName, new ForceStopReceiver(), 5L, TimeUnit.MINUTES);
                    info(String.format("<b>%s</b> forced-stop on %s", packageName, device.getName()));
                } catch (Exception e1) {
                    error("Kill fail... " + e1.getMessage());
                    e1.printStackTrace();
                }
            }
        });
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
                    runnable.run(project, device, packageName);
                }else{
                    error("No Device found");
                }
            }
        }
    }

    private static interface AdbRunnable {
        void run(Project project, IDevice device, String packageName);
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

    private static final class ForceStopReceiver extends MultiLineReceiver {

        private static final String SUCCESS_OUTPUT = "Success"; //$NON-NLS-1$
        private static final Pattern FAILURE_PATTERN = Pattern.compile("Failure\\s+\\[(.*)\\]"); //$NON-NLS-1$

        private String mErrorMessage = null;

        public ForceStopReceiver() {
        }

        @Override
        public void processNewLines(String[] lines) {
            for (String line : lines) {
                if (!line.isEmpty()) {
                    if (line.startsWith(SUCCESS_OUTPUT)) {
                        mErrorMessage = null;
                    } else {
                        Matcher m = FAILURE_PATTERN.matcher(line);
                        if (m.matches()) {
                            mErrorMessage = m.group(1);
                        } else {
                            mErrorMessage = "Unknown failure";
                        }
                    }
                }
            }
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public String getErrorMessage() {
            return mErrorMessage;
        }
    }

}
