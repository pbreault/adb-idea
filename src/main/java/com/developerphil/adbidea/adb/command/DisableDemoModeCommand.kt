package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class DisableDemoModeCommand : Command {
    override fun run(project: Project?, device: IDevice?, facet: AndroidFacet?, packageName: String?): Boolean =
            try {
                device?.executeShellCommand("am broadcast -a com.android.systemui.demo --es command exit", GenericReceiver(), 15L, TimeUnit.SECONDS)
                        ?: NotificationHelper.error("Cannont exit Demo mode on a null device")
                true
            } catch (e: Exception) {
                NotificationHelper.error("Exit Demo mode failed... " + e.message)
                false
            }
}