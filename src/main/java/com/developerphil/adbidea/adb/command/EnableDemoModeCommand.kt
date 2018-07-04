package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class EnableDemoModeCommand(
        private val hour: Int = 12,
        private val minute: Int = 34,
        private val batteryPower: Int = 100,
        private val batteryPlugged: Boolean = false,
        private val showWifiNetwork: Boolean = true,
        private val showMobileNetwork: Boolean = false,
        private val showNotifications: Boolean = false) : Command {

    override fun run(project: Project?, device: IDevice?, facet: AndroidFacet?, packageName: String?): Boolean =
            try {
                device?.run {
                    executeShellCommand("am broadcast -a com.android.systemui.demo --es command enter", GenericReceiver(), 15L, TimeUnit.SECONDS)
                    executeShellCommand("am broadcast -a com.android.systemui.demo --es command clock --es hhmm $hour$minute", GenericReceiver(), 15L, TimeUnit.SECONDS)
                    executeShellCommand("am broadcast -a com.android.systemui.demo --es command battery --es level $batteryPower --es plugged ${if (batteryPlugged) "true" else "false"}", GenericReceiver(), 15L, TimeUnit.SECONDS)
                    executeShellCommand("am broadcast -a com.android.systemui.demo --es command network --es wifi ${if (showWifiNetwork) "show" else "hide"} --es fully true --es level 4", GenericReceiver(), 15L, TimeUnit.SECONDS)
                    executeShellCommand("am broadcast -a com.android.systemui.demo --es command network --es mobile ${if (showMobileNetwork) "show" else "hide"} --es fully true --es level 4 --es datatype lte", GenericReceiver(), 15L, TimeUnit.SECONDS)
                    executeShellCommand("am broadcast -a com.android.systemui.demo --es command notifications --es visible ${if (showNotifications) "true" else "false"}", GenericReceiver(), 15L, TimeUnit.SECONDS)

                    NotificationHelper.info("Demo mode enabled")
                } ?: NotificationHelper.error("Cannont toggle Demo mode on a null device")

                true
            } catch (e: Exception) {
                NotificationHelper.error("Toggling Demo mode failed... " + e.message)
                false
            }

}