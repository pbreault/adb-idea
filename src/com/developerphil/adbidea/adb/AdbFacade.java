package com.developerphil.adbidea.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.command.*;
import com.developerphil.adbidea.ui.DeviceChooserDialog;
import com.developerphil.adbidea.ui.ModuleChooserDialogHelper;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.android.util.AndroidUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.developerphil.adbidea.ui.NotificationHelper.error;

public class AdbFacade {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("AdbIdea-%d").build());

    public static void uninstall(Project project) {
        executeOnDevice(project, new UninstallCommand());
    }

    public static void kill(Project project) {
        executeOnDevice(project, new KillCommand());
    }

    public static void reboot(Project project) {
        executeOnDevice(project, new RebootCommand());
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

    public static void startDefaultActivityWithDebugger(Project project) {
        executeOnDevice(project, new StartDefaultActivityCommandWithDebugger());
    }

    public static void restartDefaultActivityWithDebugger(Project project) {
        executeOnDevice(project, new RestartPackageCommandWithDebugger());
    }

    public static void clearDataAndRestartWithDebugger(Project project) {
        executeOnDevice(project, new ClearDataAndRestartCommandWithDebugger());
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
        List<AndroidFacet> facets = getApplicationFacets(project);
        if (!facets.isEmpty()) {
            AndroidFacet facet;
            if (facets.size() > 1) {
                facet = ModuleChooserDialogHelper.showDialogForFacets(project, facets);
                if (facet == null) {
                    return null;
                }
            } else {
                facet = facets.get(0);
            }
            String packageName = AdbUtil.computePackageName(facet);

            AndroidDebugBridge bridge = AndroidSdkUtils.getDebugBridge(project);
            if (bridge == null) {
                error("No platform configured");
                return null;
            }

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

    private static List<AndroidFacet> getApplicationFacets(Project project) {

        List<AndroidFacet> facets = Lists.newArrayList();
        for (AndroidFacet facet : AndroidUtils.getApplicationFacets(project)) {
            if (!isTestProject(facet)) {
                facets.add(facet);
            }
        }

        return facets;
    }

    private static boolean isTestProject(AndroidFacet facet) {
        return facet.getManifest() != null
                && facet.getManifest().getInstrumentations() != null
                && !facet.getManifest().getInstrumentations().isEmpty();
    }

    private static DeviceResult askUserForDevice(AndroidFacet facet, String packageName) {
        final DeviceChooserDialog chooser = new DeviceChooserDialog(facet);
        chooser.show();

        if (chooser.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
            return null;
        }

        IDevice[] selectedDevices = chooser.getSelectedDevices();
        if (selectedDevices.length == 0) {
            return null;
        }

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
