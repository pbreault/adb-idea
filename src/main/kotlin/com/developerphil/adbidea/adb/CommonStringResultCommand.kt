package com.developerphil.adbidea.adb

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.Command
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.*
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class CommonStringResultCommand(val commandStr:String,val operationDesc:String) : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            val receiver = PrintReceiver()
            device.executeShellCommand(commandStr, receiver, 15L, TimeUnit.SECONDS)
            info("$operationDesc on ${device.name}\n")
            val string = receiver.toString()
            val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NOOP_LISTENER)
            notification.notify(project)
            return true
        } catch (e1: Exception) {
            error("$operationDesc... " + e1.message)
        }

        return false
    }

}
