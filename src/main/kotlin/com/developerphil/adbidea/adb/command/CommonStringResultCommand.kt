package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.*
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class CommonStringResultCommand(private val commandStr:String,private val operationDesc:String,private val callback:((String)->Unit)? = null) : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            val receiver = PrintReceiver()
            device.executeShellCommand(commandStr, receiver, 15L, TimeUnit.SECONDS)
            if (callback==null) {
                info("$operationDesc on ${device.name}\n")
                val string = receiver.toString()
                val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NOOP_LISTENER)
                notification.notify(project)
            } else {
                callback.invoke(receiver.toString())
            }
            return true
        } catch (e1: Exception) {
            error("$operationDesc... " + e1.message)
        }

        return false
    }

}
