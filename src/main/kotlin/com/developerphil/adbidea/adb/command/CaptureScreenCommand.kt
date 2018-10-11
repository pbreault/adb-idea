package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
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
            val p = runtime.exec("adb exec-out screencap -p > "+name,null,dir)
            val fis = p.getInputStream()
            var isr = InputStreamReader(fis)
            var br = BufferedReader(isr)
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
            isr.close()
            fis.close()
//            device?.executeShellCommand("exec-out screencap -p > $path", receiver, 15L, TimeUnit.SECONDS)
//            if (receiver.toString().isNotEmpty()) {
//                val string = receiver.toString()
//                val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NotificationHelper.NOOP_LISTENER)
//                notification.notify(project)
//            }
            return true
        } catch (e1: Exception) {
            NotificationHelper.error("Put String to device... " + e1.message)
        }
        return false
    }

}