package com.developerphil.adbidea.adb

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.adb.command.Command
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

/**
 * Created by XQ Yang on 8/28/2018  3:33 PM.
 * Description :
 */

class GetApplicationListCommand(private val mParameter: String,private val callback:(List<String>)->Unit) : Command {

    private val genericReceiver = GenericReceiver()

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            device.executeShellCommand("pm list packages $mParameter", genericReceiver, 10L, TimeUnit.SECONDS)
            callback.invoke(genericReceiver.adbOutputLines)
            return true
        } catch (e1: Exception) {
            error(String.format("get Application list failure on %s",device.name))
        }
        return false
    }
}
