package com.developerphil.adbidea.action.extend

import com.developerphil.adbidea.action.AdbAction
import com.developerphil.adbidea.adb.AdbFacade
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import javax.swing.JOptionPane


/**
 * Created by XQ Yang on 8/28/2018  2:53 PM.
 * Description : input simple string to device
 */
class PutStringAction : AdbAction() {


    override fun actionPerformed(e: AnActionEvent, project: Project) {
        var result = JOptionPane.showInputDialog("Input simple string(ASCII) put to device,need open USB debugging(Security settings)")
        if (!result.isNullOrEmpty()) {
            result = result.replace(" ","")
            AdbFacade.putStringToDevice(project,result)
        }
    }

}
