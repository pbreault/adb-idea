package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jdesktop.swingx.util.OS
import org.jetbrains.android.facet.AndroidFacet
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Created by XQ Yang on 10/10/2018  10:56 AM.
 * Description :
 */
class CaptureScreenCommand(val dir: File, val name: String) : Command {
    override fun run(project: Project?, device: IDevice?, facet: AndroidFacet?, packageName: String?): Boolean {
        try {
            val runtime = Runtime.getRuntime()
            val p: Process = if (OS.isWindows()) {
                runtime.exec(arrayOf("cmd", "/C", "adb exec-out screencap -p > " + File(dir, name).absolutePath))
            } else {
                runtime.exec(arrayOf("/bin/sh","-c","adb exec-out screencap -p > "+File(dir,name).absolutePath))
            }
            val br = BufferedReader(InputStreamReader(p.inputStream))
            var line = br.readLine()
            val sb = StringBuilder()
            while (line != null) {
                sb.append(line)
                line = br.readLine()
            }
            if (sb.length > 0) {
                val notification = NotificationHelper.INFO.createNotification("ADB IDEA", sb.toString(), NotificationType.INFORMATION, NotificationHelper.NOOP_LISTENER)
                notification.notify(project)
            }
            br.close()
            return true
        } catch (e1: Exception) {
            NotificationHelper.error("Capture Screen ... " + e1.message)
        }
        return false
    }

}