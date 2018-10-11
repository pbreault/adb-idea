package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.AdbUtil.isAppInstalled
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.*
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class ActivityServiceCommand(private val mPackageName: String,private val callback:(String)->Unit) : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        var packageName = packageName
        packageName = mPackageName
        try {
            if (packageName.isNotEmpty()&&!isAppInstalled(device, packageName)) {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.name))
                return false
            }
            val receiver = PrintReceiver()
            device.executeShellCommand("dumpsys activity services $packageName", receiver, 15L, TimeUnit.SECONDS)
            info(String.format("<b>%s</b> get activity service on %s", packageName, device.name))
            val string = receiver.toString()
            callback.invoke(string)
            val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NOOP_LISTENER)
            notification.notify(project)
            return true
        } catch (e1: Exception) {
            error("get activity service... " + e1.message)
        }

        return false
    }

}
