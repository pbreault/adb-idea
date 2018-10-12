package com.developerphil.adbidea.action.extend

import com.developerphil.adbidea.action.AdbAction
import com.developerphil.adbidea.adb.AdbFacade
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by XQ Yang on 8/28/2018  2:53 PM.
 * Description : capture device screen via adb
 */
class ScreenCaptureAction : AdbAction() {

    init {
        saveDirChooserDescriptor.title = "Select capture png file save to..."
    }


    override fun actionPerformed(e: AnActionEvent?, project: Project?) {
        if (deviceName.isEmpty()) {
            AdbFacade.getSimpleInfo(project, "getprop ro.product.model", "get Device model ") { name ->
               deviceName = name.replace("\n","").replace("\r","").replace(" ","")
            }
        }
        val choose = FileChooserDialogImpl(saveDirChooserDescriptor, project)
            .choose(project, selectedFile)
        if (choose.isNotEmpty()) {
            selectedFile = choose[0]
            AdbFacade.captureScreen(project, File(selectedFile?.canonicalPath),"${deviceName}_${dateFormat.format(Date())}.png")
        }
    }

    companion object {
        private var selectedFile: VirtualFile? = null
        private var saveDirChooserDescriptor: FileChooserDescriptor = FileChooserDescriptor(false, true, false, false, false, false)
        private var dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        var deviceName = ""
    }

}
