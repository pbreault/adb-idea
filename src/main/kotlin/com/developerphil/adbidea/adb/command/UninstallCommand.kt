package com.developerphil.adbidea.adb.command

import com.android.ddmlib.InstallException
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info

class UninstallCommand : Command {
    override fun run(context: CommandContext): Boolean = with(context) {
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