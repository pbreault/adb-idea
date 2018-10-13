package com.developerphil.adbidea.adb;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.Log;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.SyncService;
import com.android.ddmlib.TimeoutException;
import com.android.tools.idea.gradle.project.sync.GradleSyncState;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.developerphil.adbidea.ui.NotificationHelper;
import com.intellij.openapi.project.Project;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.joor.Reflect;

public class AdbUtil {

    public static boolean isAppInstalled(IDevice device, String packageName) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        GenericReceiver receiver = new GenericReceiver();
        // "pm list packages com.my.package" will return one line per package installed that corresponds to this package.
        // if this list is empty, we know for sure that the app is not installed
        device.executeShellCommand("pm list packages " + packageName, receiver, 15L, TimeUnit.SECONDS);

        //TODO make sure that it is the exact package name and not a subset.
        // e.g. if our app is called com.example but there is another app called com.example.another.app, it will match and return a false positive
        return !receiver.getAdbOutputLines().isEmpty();
    }

    // The android debugger class is not available in Intellij 2016.1.
    // Nobody should use that version but it's still the minimum "supported" version since android studio 2.2
    // shares the same base version.
    public static Boolean isDebuggingAvailable() {
        try {
            Reflect.on("com.android.tools.idea.run.editor.AndroidDebugger").get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isGradleSyncInProgress(Project project) {
        try {
            return GradleSyncState.getInstance(project).isSyncInProgress();
        } catch (Throwable t) {
            NotificationHelper.info("Couldn't determine if a gradle sync is in progress");
            return false;
        }
    }


    public static void pullFile(IDevice  device,String remote, String local,SyncService.ISyncProgressMonitor monitor) throws IOException, AdbCommandRejectedException, com.android.ddmlib.TimeoutException, SyncException {
        SyncService sync = null;
        try {
            String targetFileName =(new File(remote)).getName();
            Log.d(targetFileName, String.format("Downloading %1$s from device '%2$s'", targetFileName, device.getSerialNumber()));
            sync = device.getSyncService();
            if (sync == null) {
                throw new IOException("Unable to open sync connection!");
            }
            String message = String.format("Downloading file from device '%1$s'", device.getSerialNumber());
            Log.d("Device", message);
            sync.pullFile(remote, local, monitor);
        } catch (com.android.ddmlib.TimeoutException var11) {
            Log.e("Device", "Error during Sync: timeout.");
            throw var11;
        } catch (SyncException var12) {
            Log.e("Device", String.format("Error during Sync: %1$s", var12.getMessage()));
            throw var12;
        } catch (IOException var13) {
            Log.e("Device", String.format("Error during Sync: %1$s", var13.getMessage()));
            throw var13;
        } finally {
            if (sync != null) {
                sync.close();
            }
        }
    }

}
