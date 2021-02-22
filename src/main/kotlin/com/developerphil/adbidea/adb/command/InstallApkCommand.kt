package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.android.ddmlib.InstallException
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by XQ Yang on 2018-10-9 15:01:03
 * Description :
 */

class InstallApkCommand(val apks: List<File>) : Command {
    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            device.installPackages(apks, true, listOf(), 15, TimeUnit.SECONDS)
            info(String.format("Install %d apk file to %s", apks.size, device.name))
            return true
        } catch (e1: InstallException) {
            error("Install fail... " + e1.message)
        }
        return false
    }


}
