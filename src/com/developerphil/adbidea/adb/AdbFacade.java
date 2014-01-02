package com.developerphil.adbidea.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.developerphil.adbidea.ui.DeviceChooserDialog;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pbreault on 10/6/13.
 */
public class AdbFacade {

    public static void uninstall(Project project) {
        executeOnDevice(project, new AdbRunnable() {
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
        executeOnDevice(project, new AdbRunnable() {
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
        executeOnDevice(project, new AdbRunnable() {
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

    public static void clearData(Project project) {
        executeOnDevice(project, new AdbRunnable() {
            @Override
            public void run(Project project, IDevice device, AndroidFacet facet, String packageName) {
                try {
                    device.executeShellCommand("pm clear " + packageName, new GenericReceiver(), 5L, TimeUnit.MINUTES);
                    info(String.format("<b>%s</b> cleared data for app on %s", packageName, device.getName()));
                } catch (Exception e1) {
                    error("Start fail... " + e1.getMessage());
                    e1.printStackTrace();
                }
            }
        });
    }

    private static void executeOnDevice(Project project, AdbRunnable runnable) {
        DeviceResult result = getDevice(project);
        if (result != null) {
            runnable.run(project, result.device, result.facet, result.packageName);
        } else {
            error("No Device found");
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


    private static DeviceResult getDevice(Project project) {
        List<AndroidFacet> facets = AndroidUtils.getApplicationFacets(project);
        if (!facets.isEmpty()) {
            AndroidFacet facet = facets.get(0);
            String packageName = facet.getManifest().getPackage().getXmlAttributeValue().getValue();

            AndroidDebugBridge bridge = facet.getDebugBridge();
            if (bridge.isConnected() && bridge.hasInitialDeviceList()) {
                IDevice[] devices = bridge.getDevices();
                if (devices.length == 1) {
                    return new DeviceResult(devices[0], facet, packageName);
                } else if (devices.length > 1) {
                    return askUserForDevice(facet, packageName);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private static DeviceResult askUserForDevice(AndroidFacet facet, String packageName) {
        final DeviceChooserDialog chooser = new DeviceChooserDialog(facet, true);
        chooser.show();

        if (chooser.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
            return null;
        }

        IDevice[] selectedDevices = chooser.getSelectedDevices();
        if (selectedDevices.length == 0) {
            return null;
        }

        //TODO support sending to multiple devices at once
        return new DeviceResult(selectedDevices[0], facet, packageName);
    }

    private static final class DeviceResult {
        private final IDevice device;
        private final AndroidFacet facet;
        private final String packageName;

        private DeviceResult(IDevice device, AndroidFacet facet, String packageName) {
            this.device = device;
            this.facet = facet;
            this.packageName = packageName;
        }
    }

}
