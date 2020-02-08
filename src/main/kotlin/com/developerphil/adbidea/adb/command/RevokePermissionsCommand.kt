package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.AdbUtil
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class RevokePermissionsCommand : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            if (deviceHasMarshmallow(device)) if (AdbUtil.isAppInstalled(device, packageName)) {
                val shellOutputReceiver = GenericReceiver()
                device.executeShellCommand("dumpsys package $packageName", shellOutputReceiver, 15L, TimeUnit.SECONDS)
                shellOutputReceiver.adbOutputLines.stream() //only granted permissions, they come in "android.permission.CAMERA: granted=true"
                        .filter { s: String -> s.contains("permission") }.filter { s: String -> s.contains("granted=true") } //just the permission name is important
                        .map { s: String -> s.split(":".toRegex()).toTypedArray()[0].trim { it <= ' ' } }
                        .forEach { s: String ->
                            try {
                                device.executeShellCommand("pm revoke $packageName $s", GenericReceiver(), 15L, TimeUnit.SECONDS)
                                NotificationHelper.info(String.format("Permission <b>%s</b> revoked on %s", s, device.name))
                            } catch (e: Exception) {
                                error(String.format("Revoking %s failed on %s: %s", s, device.name, e.message))
                            }
                        }
                return true
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.name))
            } else {
                error(String.format("%s must be at least api level 23", device.name))
            }
        } catch (e1: Exception) {
            error("Revoking permissions fail... " + e1.message)
        }
        return false
    }

    private fun deviceHasMarshmallow(device: IDevice) = device.version.apiLevel >= 23
}