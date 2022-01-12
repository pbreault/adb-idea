package com.developerphil.adbidea.action.extend

import com.developerphil.adbidea.ObjectGraph
import com.developerphil.adbidea.action.AdbAction
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

/**
 * @describe
 * @author  longforus
 * @date 10/29/2018  4:21 PM
 */
class ClearCurrentRememberDeviceAction : AdbAction() {

    override fun actionPerformed(e: AnActionEvent, project: Project) {
        project.getComponent(ObjectGraph::class.java)
            .clearSelectedDevice()
        NotificationHelper.info("clear success")
    }

}