package com.developerphil.adbidea.action.extend

import com.developerphil.adbidea.action.AdbAction
import com.developerphil.adbidea.ui.DeviceInfoFrame
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project


/**
 * Created by XQ Yang on 8/28/2018  2:53 PM.
 * Description : input simple string to device
 */
class ShowDeviceInfoAction : AdbAction() {


    override fun actionPerformed(e: AnActionEvent?, project: Project?) {
        val form = DeviceInfoFrame(project)
        form.pack()
        form.isVisible = true
    }

}
