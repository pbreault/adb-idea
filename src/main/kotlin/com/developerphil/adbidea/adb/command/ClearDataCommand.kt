package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.AdbUtil
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class ClearDataCommand(val realPackageName:String = "") : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        var pn = packageName
        if (realPackageName.isNotEmpty()) {
            pn = realPackageName
        }
        try {
            if (AdbUtil.isAppInstalled(device, pn)) {
                device.executeShellCommand("pm clear $pn", GenericReceiver(), 15L, TimeUnit.SECONDS)
                NotificationHelper.info(String.format("<b>%s</b> cleared data for app on %s", pn, device.name))
                return true
            } else {
                NotificationHelper.error(String.format("<b>%s</b> is not installed on %s", pn, device.name))
            }
        } catch (e1: Exception) {
            NotificationHelper.error("Clear data failed... " + e1.message)
        }

        return false
    }

}