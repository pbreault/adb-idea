package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.AdbUtil.isAppInstalled
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.NOOP_LISTENER
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit
/**
 * @describe
 * @author  Void Young
 * @date 2018-10-13 16:48:56
 */

class ActivityServiceCommand(private val mPackageName: String,private val callback:(String)->Unit) : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            if (mPackageName.isNotEmpty()&&!isAppInstalled(device, mPackageName)) {
                error(String.format("<b>%s</b> is not installed on %s", mPackageName, device.name))
                return false
            }
            val receiver = PrintReceiver()
            device.executeShellCommand("dumpsys activity services $mPackageName", receiver, 15L, TimeUnit.SECONDS)
            NotificationHelper.info(String.format("<b>%s</b> get activity service on %s", mPackageName, device.name))
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
