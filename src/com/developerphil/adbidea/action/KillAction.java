package com.developerphil.adbidea.action;

import com.android.ddmlib.*;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pbreault on 9/28/13.
 */
public class KillAction extends AnAction {

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
                        device.executeShellCommand("am force-stop " + packageName, new ForceStopReceiver(), 5L, TimeUnit.MINUTES);
                        info(String.format("<b>%s</b> forced-stop on %s", packageName, deviceName));
                    } catch (Exception e1) {
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



    /**
     * Output receiver for "pm install package.apk" command line.
     */
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
