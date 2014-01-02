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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.developerphil.adbidea.ui.NotificationHelper.error;

/**
 * Created by pbreault on 10/6/13.
 */
public class AdbFacade {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactory() {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "ADB_IDEA#" + counter.incrementAndGet());
        }
    });

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

    public static void clearDataAndRestart(Project project) {
        executeOnDevice(project, new ClearDataAndRestartCommand());
    }

    private static void executeOnDevice(final Project project, final Command runnable) {
        final DeviceResult result = getDevice(project);
        if (result != null) {
            for (final IDevice device : result.devices) {
                EXECUTOR.submit(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run(project, device, result.facet, result.packageName);
                    }

                });
            }
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
                    return new DeviceResult(devices, facet, packageName);
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
        return new DeviceResult(selectedDevices, facet, packageName);
    }

    private static final class DeviceResult {
        private final IDevice[] devices;
        private final AndroidFacet facet;
        private final String packageName;

        private DeviceResult(IDevice[] devices, AndroidFacet facet, String packageName) {
            this.devices = devices;
            this.facet = facet;
            this.packageName = packageName;
        }
    }

}
