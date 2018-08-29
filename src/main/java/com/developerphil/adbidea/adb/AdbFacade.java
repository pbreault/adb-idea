package com.developerphil.adbidea.adb;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.ObjectGraph;
import com.developerphil.adbidea.adb.command.ClearDataAndRestartCommand;
import com.developerphil.adbidea.adb.command.ClearDataCommand;
import com.developerphil.adbidea.adb.command.Command;
import com.developerphil.adbidea.adb.command.CommandList;
import com.developerphil.adbidea.adb.command.GrantPermissionsCommand;
import com.developerphil.adbidea.adb.command.KillCommand;
import com.developerphil.adbidea.adb.command.RestartPackageCommand;
import com.developerphil.adbidea.adb.command.RevokePermissionsAndRestartCommand;
import com.developerphil.adbidea.adb.command.RevokePermissionsCommand;
import com.developerphil.adbidea.adb.command.StartDefaultActivityCommand;
import com.developerphil.adbidea.adb.command.UninstallCommand;
import com.developerphil.adbidea.ui.NotificationHelper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intellij.openapi.project.Project;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.developerphil.adbidea.adb.AdbUtil.isGradleSyncInProgress;
import static com.developerphil.adbidea.ui.NotificationHelper.error;

public class AdbFacade {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("AdbIdea-%d").build());

    public static void uninstall(Project project, String packageName) {
        executeOnDevice(project, new UninstallCommand(packageName));
    }

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
    public static void getPackageDetail(Project project,String packageName,Function1<? super String, Unit> callback) {
        executeOnDevice(project, new PackageDetailCommand(packageName,callback));
    }
    public static void getPackagePath(Project project,String packageName,Function1<? super String, Unit> callback) {
        executeOnDevice(project, new PackagePathCommand(packageName,callback));
    }
    public static void getActivityService(Project project,String packageName,Function1<? super String, Unit> callback) {
        executeOnDevice(project, new ActivityServiceCommand(packageName,callback));
    }

    public static void clearDataAndRestart(Project project) {
        executeOnDevice(project, new ClearDataAndRestartCommand());
    }


    public static void getAllApplicationList(Project project,String parameter, Function1<? super List<String>, Unit> callback){
        executeOnDevice(project, new GetApplicationListCommand(parameter,callback));
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

    public static void clearData(Project project, String realPackageName) {
        executeOnDevice(project, new ClearDataCommand(realPackageName));
    }
}
