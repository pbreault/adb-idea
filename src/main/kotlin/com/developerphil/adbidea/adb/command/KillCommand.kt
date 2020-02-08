package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.AdbUtil
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class KillCommand : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            if (AdbUtil.isAppInstalled(device, packageName)) {
                device.executeShellCommand("am force-stop $packageName", GenericReceiver(), 15L, TimeUnit.SECONDS)
                info(String.format("<b>%s</b> forced-stop on %s", packageName, device.name))
                return true
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.name))
            }
        } catch (e1: Exception) {
            error("Kill fail... " + e1.message)
        }
        return false
    }
}