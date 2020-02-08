package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.android.ddmlib.InstallException
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet

class UninstallCommand : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            val errorCode = device.uninstallPackage(packageName)
            if (errorCode == null) {
                info(String.format("<b>%s</b> uninstalled on %s", packageName, device.name))
                return true
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.name))
            }
        } catch (e1: InstallException) {
            error("Uninstall fail... " + e1.message)
        }
        return false
    }
}