package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.*
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit
/**
 * @describe
 * @author  Void Young
 * @date 2018-10-13 16:48:56
 */

class ForegroundActivityCommand(private val callback:(String)->Unit) : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            val receiver = PrintReceiver()
            device.executeShellCommand("dumpsys activity activities | grep mFocusedActivity", receiver, 15L, TimeUnit.SECONDS)
            info(String.format(" get foreground Activity on %s",device.name))
            val string = receiver.toString()
            callback.invoke(string)
            val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NOOP_LISTENER)
            notification.notify(project)
            return true
        } catch (e1: Exception) {
            error("Get foreground Activity... " + e1.message)
        }
        return false
    }

}
