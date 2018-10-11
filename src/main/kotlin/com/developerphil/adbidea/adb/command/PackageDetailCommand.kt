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

class PackageDetailCommand(private val mPackageName: String,private val callback:(String)->Unit) : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        var packageName = packageName
        packageName = mPackageName
        try {
            if (isAppInstalled(device, packageName)) {
                val receiver = PrintReceiver()
                device.executeShellCommand("dumpsys package $packageName", receiver, 15L, TimeUnit.SECONDS)
                info(String.format("<b>%s</b> get package detail on %s", packageName, device.name))
                val string = receiver.toString()
                callback.invoke(string)
                val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NOOP_LISTENER)
                notification.notify(project)
                return true
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.name))
            }
        } catch (e1: Exception) {
            error("Get package detail... " + e1.message)
        }

        return false
    }

}
