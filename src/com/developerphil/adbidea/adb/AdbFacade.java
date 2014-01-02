package com.developerphil.adbidea.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.command.*;
import com.developerphil.adbidea.ui.DeviceChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.util.List;

import static com.developerphil.adbidea.ui.NotificationHelper.error;

/**
 * Created by pbreault on 10/6/13.
 */
public class AdbFacade {

    public static void uninstall(Project project) {
        executeOnDevice(project, new UninstallCommand());
    }

    public static void kill(Project project) {
        executeOnDevice(project, new KillCommand());
    }

    public static void startDefaultActivity(Project project) {
        executeOnDevice(project, new StartDefaultActivityCommand());
    }

    public static void restartDefaultActivity(Project project) {
        executeOnDevice(project, new RestartPackageCommand());
    }

    public static void clearData(Project project) {
        executeOnDevice(project, new ClearDataCommand());
    }

    private static void executeOnDevice(Project project, Command runnable) {
        DeviceResult result = getDevice(project);
        if (result != null) {
            runnable.run(project, result.device, result.facet, result.packageName);
        } else {
            error("No Device found");
        }
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
