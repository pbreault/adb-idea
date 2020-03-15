package com.developerphil.adbidea.adb

import com.android.ddmlib.*
import com.android.tools.idea.gradle.project.sync.GradleSyncState
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.intellij.openapi.project.Project
import org.joor.Reflect
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


object AdbUtil {
    @Throws(TimeoutException::class, AdbCommandRejectedException::class, ShellCommandUnresponsiveException::class, IOException::class)
    fun isAppInstalled(device: IDevice, packageName: String): Boolean {
        val receiver = GenericReceiver()
        // "pm list packages com.my.package" will return one line per package installed that corresponds to this package.
        // if this list is empty, we know for sure that the app is not installed
        device.executeShellCommand("pm list packages $packageName", receiver, 15L, TimeUnit.SECONDS)
        //TODO make sure that it is the exact package name and not a subset.
        // e.g. if our app is called com.example but there is another app called com.example.another.app, it will match and return a false positive
        return receiver.adbOutputLines.isNotEmpty()
    }

    // The android debugger class is not available in Intellij 2016.1.
    // Nobody should use that version but it's still the minimum "supported" version since android studio 2.2
    // shares the same base version.
    val isDebuggingAvailable: Boolean
        get() = try {
            Reflect.on("com.android.tools.idea.run.editor.AndroidDebugger").get<Any>()
            true
        } catch (e: Exception) {
            false
        }

    fun isGradleSyncInProgress(project: Project): Boolean {
        return try {
            GradleSyncState.getInstance(project).isSyncInProgress
        } catch (t: Throwable) {
            info("Couldn't determine if a gradle sync is in progress")
            false
        }
    }


    @Throws(IOException::class, AdbCommandRejectedException::class, com.android.ddmlib.TimeoutException::class, SyncException::class)
    fun pullFile(device: IDevice, remote: String, local: String, monitor: SyncService.ISyncProgressMonitor) {
        var sync: SyncService? = null
        try {
            val targetFileName = File(remote).getName()
            Log.d(targetFileName, String.format("Downloading %1\$s from device '%2\$s'", targetFileName, device.serialNumber))
            sync = device.syncService
            if (sync == null) {
                throw IOException("Unable to open sync connection!")
            }
            val message = String.format("Downloading file from device '%1\$s'", device.serialNumber)
            Log.d("Device", message)
            sync.pullFile(remote, local, monitor)
        } catch (var11: com.android.ddmlib.TimeoutException) {
            Log.e("Device", "Error during Sync: timeout.")
            throw var11
        } catch (var12: SyncException) {
            Log.e("Device", String.format("Error during Sync: %1\$s", var12.message))
            throw var12
        } catch (var13: IOException) {
            Log.e("Device", String.format("Error during Sync: %1\$s", var13.message))
            throw var13
        } finally {
            sync?.close()
        }
    }
}