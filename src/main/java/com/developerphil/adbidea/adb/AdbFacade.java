package com.developerphil.adbidea.adb;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.ObjectGraph;
import com.developerphil.adbidea.adb.command.*;
import com.developerphil.adbidea.ui.NotificationHelper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intellij.openapi.project.Project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.developerphil.adbidea.adb.AdbUtil.isGradleSyncInProgress;
import static com.developerphil.adbidea.ui.NotificationHelper.error;

public class AdbFacade {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("AdbIdea-%d").build());

    public static void uninstall(Project project) {
        executeOnDevice(project, new UninstallCommand());
    }

    public static void kill(Project project) {
        executeOnDevice(project, new KillCommand());
    }

    public static void grantPermissions(Project project) {
        executeOnDevice(project, new GrantPermissionsCommand());
    }

    public static void revokePermissions(Project project) {
        executeOnDevice(project, new RevokePermissionsCommand());
    }

    public static void revokePermissionsAndRestart(Project project) {
        executeOnDevice(project, new RevokePermissionsAndRestartCommand());
    }

    public static void startDefaultActivity(Project project) {
        executeOnDevice(project, new StartDefaultActivityCommand(false));
    }

    public static void startDefaultActivityWithDebugger(Project project) {
        executeOnDevice(project, new StartDefaultActivityCommand(true));
    }

    public static void restartDefaultActivity(Project project) {
        executeOnDevice(project, new RestartPackageCommand());
    }

    public static void restartDefaultActivityWithDebugger(Project project) {
        executeOnDevice(project, new CommandList(new KillCommand(), new StartDefaultActivityCommand(true)));
    }

    public static void clearData(Project project) {
        executeOnDevice(project, new ClearDataCommand());
    }

    public static void clearDataAndRestart(Project project) {
        executeOnDevice(project, new ClearDataAndRestartCommand());
    }

    public static void toggleTalkBack(Project project) {
        executeOnDevice(project, new ToggleTalkBackCommand());
    }

    public static void openDeepLink(Project project, String deepLink) {
        executeOnDevice(project, new OpenDeepLinkCommand(deepLink));
    }

    private static void executeOnDevice(final Project project, final Command runnable) {

        if (isGradleSyncInProgress(project)) {
            NotificationHelper.error("Gradle sync is in progress");
            return;
        }

        final DeviceResult result = project.getComponent(ObjectGraph.class)
                .getDeviceResultFetcher()
                .fetch();

        if (result != null) {
            for (final IDevice device : result.getDevices()) {
                EXECUTOR.submit((Runnable) () -> runnable.run(project, device, result.getFacet(), result.getPackageName()));
            }
        } else {
            error("No Device found");
        }
    }
}
