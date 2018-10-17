package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.android.ddmlib.SyncService
import com.developerphil.adbidea.adb.AdbUtil
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.openFileExplorer
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.NOOP_LISTENER
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by XQ Yang on 2018-10-9 15:00:43
 * Description :  pull file to computer
 */

class PullFileCommand(private val remotePath: String,val localFile: File, val deleteRemoteFile:Boolean) : Command {

    lateinit var mDevice: IDevice
    val receiver = PrintReceiver()

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        mDevice = device
        try {
            AdbUtil.pullFile(device,remotePath,localFile.absolutePath,object : SyncService.ISyncProgressMonitor{
                override fun startSubTask(p0: String?) {

                }
                override fun start(p0: Int) {

                }

                override fun stop() {
                    openFileExplorer(localFile.parentFile.absolutePath)
                    if (deleteRemoteFile) {
                        device.executeShellCommand("rm $remotePath",receiver,10L,TimeUnit.SECONDS)
                    }
                }

                override fun isCanceled(): Boolean  = false

                override fun advance(p0: Int) {

                }

            })
            val string = receiver.toString()
            val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NOOP_LISTENER)
            notification.notify(project)
            return true
        } catch (e1: Exception) {
            error("Pull File... " + e1.message)
        }
        return false
    }


}
