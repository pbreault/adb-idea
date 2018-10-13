package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.android.ddmlib.ScreenRecorderOptions
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
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by XQ Yang on 2018-10-9 15:00:43
 * Description : record screen to computer
 */

class ScreenRecordCommand(private val localPath: File, videoName: String, val length: Int, val showTouches:Boolean) : Command {

    lateinit var mDevice: IDevice
    val receiver = PrintReceiver()
    private val remotePath = "/sdcard/$videoName"

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        mDevice = device
        try {
            val options = ScreenRecorderOptions.Builder().setTimeLimit(length.toLong(), TimeUnit.SECONDS).setShowTouches(showTouches).build()
            device.startScreenRecorder(remotePath, options, receiver)
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    AdbUtil.pullFile(device,remotePath,localPath.absolutePath,object : SyncService.ISyncProgressMonitor{
                        override fun startSubTask(p0: String?) {

                        }
                        override fun start(p0: Int) {

                        }

                        override fun stop() {
                            openFileExplorer(localPath.parentFile.absolutePath)
                            device.executeShellCommand("rm $remotePath",receiver,10L,TimeUnit.SECONDS)
                        }

                        override fun isCanceled(): Boolean  = false

                        override fun advance(p0: Int) {

                        }

                    })
                }
            }, (length + 1) * 1000L)
            val string = receiver.toString()
            val notification = NotificationHelper.INFO.createNotification("ADB IDEA", string, NotificationType.INFORMATION, NOOP_LISTENER)
            notification.notify(project)
            return true
        } catch (e1: Exception) {
            error("Record Screen... " + e1.message)
        }
        return false
    }


}
