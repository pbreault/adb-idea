package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.AdbUtil
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class GrantPermissionsCommand : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            if (deviceHasMarshmallow(device)) if (AdbUtil.isAppInstalled(device, packageName)) {
                val shellOutputReceiver = GenericReceiver()
                device.executeShellCommand("dumpsys package $packageName", shellOutputReceiver, 15L, TimeUnit.SECONDS)
                val adbOutputLines = getRequestedPermissions(shellOutputReceiver.adbOutputLines)
                NotificationHelper.info(adbOutputLines.toTypedArray().contentToString())
                adbOutputLines.forEach(Consumer { s: String ->
                    try {
                        device.executeShellCommand("pm grant $packageName $s", GenericReceiver(), 15L, TimeUnit.SECONDS)
                        NotificationHelper.info(String.format("Permission <b>%s</b> granted on %s", s, device.name))
                    } catch (e: Exception) {
                        NotificationHelper.error(String.format("Granting %s failed on %s: %s", s, device.name, e.message))
                    }
                })
                return true
            } else {
                NotificationHelper.error(String.format("<b>%s</b> is not installed on %s", packageName, device.name))
            } else {
                NotificationHelper.error(String.format("%s must be at least api level 23", device.name))
            }
        } catch (e1: Exception) {
            NotificationHelper.error("Granting permissions fail... " + e1.message)
        }
        return false
    }

    private fun deviceHasMarshmallow(device: IDevice): Boolean {
        return device.version.apiLevel >= 23
    }

    private fun getRequestedPermissions(list: List<String>): List<String> {
        var requestedPermissionsSection = false
        val requestPermissions: MutableList<String> = ArrayList()
        for (s in list) {
            if (!s.contains(".permission.")) {
                requestedPermissionsSection = false
            }
            if (s.contains("requested permissions:")) {
                requestedPermissionsSection = true
                continue
            }
            if (requestedPermissionsSection) {
                val permissionName = s.replace(":", "").trim { it <= ' ' }
                requestPermissions.add(permissionName)
            }
        }
        return requestPermissions
    }
}