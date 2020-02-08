package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.AdbUtil
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class ClearDataCommand : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            if (AdbUtil.isAppInstalled(device, packageName)) {
                device.executeShellCommand("pm clear $packageName", GenericReceiver(), 15L, TimeUnit.SECONDS)
                NotificationHelper.info(String.format("<b>%s</b> cleared data for app on %s", packageName, device.name))
                return true
            } else {
                NotificationHelper.error(String.format("<b>%s</b> is not installed on %s", packageName, device.name))
            }
        } catch (e1: Exception) {
            NotificationHelper.error("Clear data failed... " + e1.message)
        }
        return false
    }
}