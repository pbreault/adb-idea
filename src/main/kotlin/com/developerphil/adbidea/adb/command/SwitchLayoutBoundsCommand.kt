package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.adb.command.receiver.ResponseHolder
import com.developerphil.adbidea.adb.command.receiver.ResponseReceiver
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit



class SwitchLayoutBoundsCommand() : Command {


    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            val receiver = ResponseHolder()
            device.executeShellCommand("getprop debug.layout", ResponseReceiver(receiver), 30L, TimeUnit.SECONDS)
            val isAlreadyOn = receiver.response.contains("true")
            val cmd = if(isAlreadyOn){
                "setprop debug.layout false && service call activity 1599295570" //SYSPROPS_TRANSACTION
            } else {
                "setprop debug.layout true && service call activity 1599295570" //SYSPROPS_TRANSACTION
            }
            device.executeShellCommand(cmd, ResponseReceiver(receiver), 30L, TimeUnit.SECONDS)

            info(String.format("<b>%s</b> on %s", if(isAlreadyOn)"Hide layout bound" else "Show layout bound", device.name))
            return true
        } catch (e: Exception) {
            error("Failure while attempting to switch layout bounds on ${device.name}: " + e.message)
        }
        return false
    }
}
