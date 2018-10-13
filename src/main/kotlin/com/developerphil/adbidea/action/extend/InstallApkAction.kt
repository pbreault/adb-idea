package com.developerphil.adbidea.action.extend

import com.developerphil.adbidea.action.AdbAction
import com.developerphil.adbidea.adb.AdbFacade
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl
import com.intellij.openapi.project.Project
import org.jdesktop.swingx.util.OS
import java.io.File


/**
 * Created by XQ Yang on 8/28/2018  2:53 PM.
 * Description :
 */
class InstallApkAction : AdbAction() {


    private lateinit var apkChooserDescriptor: FileChooserDescriptor

    override fun actionPerformed(e: AnActionEvent?, project: Project?) {
        // Set 'chooseFolders' depend on OS, because macOS application represents a directory.
        apkChooserDescriptor = FileChooserDescriptor(true, OS.isMacOSX(), false, false, false, true)
        apkChooserDescriptor.title = "selected apk file to install,support multiple choose"
        val apks = FileChooserDialogImpl(apkChooserDescriptor, project)
            .choose(project)
        if (apks.isNotEmpty()) {
            val apkList = mutableListOf<File>()
            apks.forEach {
                if (it.name.endsWith(".apk")) {
                    apkList.add(File(it.canonicalPath))
                }
            }
            if (apkList.isNotEmpty()) {
                AdbFacade.installApk(project,apkList)
            }
        }
    }
}