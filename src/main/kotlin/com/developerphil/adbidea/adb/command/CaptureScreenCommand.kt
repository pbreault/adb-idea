package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.AdbFacade
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import javafx.application.Application
import org.jetbrains.android.facet.AndroidFacet
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by XQ Yang on 10/10/2018  10:56 AM.
 * Description :
 */
class CaptureScreenCommand(val localDir: File, val fileName: String) : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet?, packageName: String?): Boolean {
        try {
            val remotePath = "/sdcard/$fileName"
            val receiver = PrintReceiver()
            device.executeShellCommand("screencap -p > $remotePath", receiver, 15L, TimeUnit.SECONDS)
            NotificationHelper.info(String.format("Capture screen on %s", device.name))
            val string = receiver.toString()
            val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NotificationHelper.NOOP_LISTENER)
            notification.notify(project)
            Thread.sleep(1000L)
            ApplicationManager.getApplication().invokeLater {
                AdbFacade.pullFile(project, remotePath, File(localDir,fileName), true)
            }
            return true
        } catch (e1: Exception) {
            NotificationHelper.error("Capture Screen ... " + e1.message)
        }
        return false
    }

}