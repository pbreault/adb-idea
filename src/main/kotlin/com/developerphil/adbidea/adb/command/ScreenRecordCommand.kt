package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.android.ddmlib.ScreenRecorderOptions
import com.developerphil.adbidea.adb.command.receiver.PrintReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import com.developerphil.adbidea.ui.NotificationHelper.NOOP_LISTENER
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.intellij.ide.actions.OpenFileAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by XQ Yang on 2018-10-9 15:00:43
 * Description :
 */

class ScreenRecordCommand(private val savePath: String, videoName: String, val length: Int, val showTouches: Boolean = false) : Command {

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
                    device.pullFile(remotePath, savePath)
                    OpenFileAction.openFile(savePath, project)
                    //todo:need optimize
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            device.executeShellCommand("rm $remotePath",receiver,10L,TimeUnit.SECONDS)
                        }
                    }, 5 * 1000L)
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
