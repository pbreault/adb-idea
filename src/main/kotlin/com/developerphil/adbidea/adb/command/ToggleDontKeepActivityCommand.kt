package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class ToggleDontKeepActivityCommand : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            val getCurrentSettingReceiver = GenericReceiver()
            device.executeShellCommand("settings get global always_finish_activities", getCurrentSettingReceiver, 15L, TimeUnit.SECONDS)
            val currentValue = getCurrentSettingReceiver.adbOutputLines.lastOrNull { it.isNotBlank() }?.toIntOrNull() ?: 0
            val newValue = (currentValue + 1) % 2
            device.executeShellCommand("settings put global always_finish_activities $newValue", GenericReceiver(), 15L, TimeUnit.SECONDS)
            info("Toggle Don't Keep Activity from $currentValue to $newValue")
            return true
        } catch (e1: Exception) {
            error("Toggle Don't Keep Activity fail... " + e1.message)
        }
        return false
    }
}