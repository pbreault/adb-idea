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
 * Created by XQ Yang on 2018-10-9 15:00:43
 * Description :
 */

class ForceStopCommand(private val mPackageName: String) : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            if (isAppInstalled(device, mPackageName)) {
                val receiver = PrintReceiver()
                device.executeShellCommand("am force-stop $mPackageName", receiver, 15L, TimeUnit.SECONDS)
                NotificationHelper.info(String.format("ForceStop <b>%s</b> on %s", mPackageName, device.name))
                val string = receiver.toString()
                val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NOOP_LISTENER)
                notification.notify(project)
                return true
            } else {
                error(String.format("<b>%s</b> is not installed on %s", mPackageName, device.name))
            }
        } catch (e1: Exception) {
            error("Force Stop... " + e1.message)
        }
    }

}
