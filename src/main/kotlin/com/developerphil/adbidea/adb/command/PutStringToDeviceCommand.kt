package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

/**
 * Created by XQ Yang on 10/10/2018  10:56 AM.
 * Description :
 */
class PutStringToDeviceCommand(val str:String):Command{
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            val receiver = PrintReceiver()
            device?.executeShellCommand("input text $str", receiver, 15L, TimeUnit.SECONDS)
            if (!receiver.toString().isNullOrEmpty()) {
                NotificationHelper.error("Put String to device :\n $receiver")
            }
            return true
        } catch (e1: Exception) {
            NotificationHelper.error("Put String to device... " + e1.message)
        }
        return false
    }

}