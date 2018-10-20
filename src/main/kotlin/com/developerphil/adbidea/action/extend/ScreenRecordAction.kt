package com.developerphil.adbidea.action.extend

import com.android.tools.idea.gradle.editor.value.SdkValueManager
import com.developerphil.adbidea.action.AdbAction
import com.developerphil.adbidea.adb.AdbFacade
import com.developerphil.adbidea.terminal.CommandBuilder
import com.developerphil.adbidea.terminal.Environment
import com.developerphil.adbidea.ui.RecordOptionDialog
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jdesktop.swingx.util.OS
import org.jetbrains.android.sdk.AndroidSdkUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by XQ Yang on 8/28/2018  2:53 PM.
 * Description : record device screen via adb
 */
class ScreenRecordAction : AdbAction() {
    private var deviceName = ""

    val videoName: String by lazy { "${deviceName}_${dateFormat.format(Date())}.mp4" }
    val remotePath: String by lazy { "/sdcard/$videoName" }
    var getFiled = false
    override fun actionPerformed(e: AnActionEvent?, project: Project) {
        AdbFacade.getDeviceModel(project) { model: String ->
            deviceName = model
        }
        val dialog = RecordOptionDialog { deleteRemoteFile ->
            saveDirChooserDescriptor.title = "Select $videoName save to..."
            val choose = FileChooserDialogImpl(saveDirChooserDescriptor, project)
                    .choose(project, selectedFile)
            if (choose.isNotEmpty()) {
                selectedFile = choose[0]
                AdbFacade.pullFile(project, remotePath, File(selectedFile?.canonicalPath, videoName), deleteRemoteFile)
            }
        }
        dialog.onStartListener = {
            if (OS.isLinux()) {
                val adb = AndroidSdkUtils.getAdb(project)
                if (adb==null){
                    getFiled = true
                    throw RuntimeException("Adb is null")
                }else{
                    Runtime.getRuntime().exec("gnome-terminal --window --title=Press_Ctrl+C_stop_adb_screenrecord -- ${adb.absolutePath} shell screenrecord $remotePath")
                }
            } else {
                try {
                    val env = Environment.environment
                    val command = CommandBuilder.createCommand(env, "adb shell screenrecord $remotePath")
                    command.execute()
                } catch (e: IOException) {
                    getFiled = true
                    throw RuntimeException("Failed to execute the command!", e)
                }
            }
        }
        dialog.pack()
        dialog.isVisible = true
    }

    companion object {
        private var selectedFile: VirtualFile? = null
        private var saveDirChooserDescriptor: FileChooserDescriptor = FileChooserDescriptor(false, true, false, false, false, false)
        private var dateFormat = SimpleDateFormat("yyyyMMddHHmmss")

    }


}
