package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.NOOP_LISTENER
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class MonkeyTestCommand(private val mPackageName: String, private val count: Int, private val callback: (String) -> Unit) : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            val receiver = PrintReceiver()
            val sb = StringBuilder("monkey ")
            if (mPackageName.isNotEmpty()) {
                sb.append("-p $mPackageName ")
            }else if (packageName.isNotEmpty()) {
                sb.append("-p $packageName ")
            }
            sb.append("-v $count")
            device.executeShellCommand(sb.toString(), receiver, 15L, TimeUnit.SECONDS)
            NotificationHelper.info(String.format(" start monkey test on %s", device.name))
            val string = receiver.toString()
            callback.invoke(string)
            val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NOOP_LISTENER)
            notification.notify(project)
            return true
        } catch (e1: Exception) {
            error("Start monkey test... " + e1.message)
        }
    }

}
