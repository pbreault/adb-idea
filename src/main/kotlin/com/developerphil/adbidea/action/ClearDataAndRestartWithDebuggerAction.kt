package com.developerphil.adbidea.action

import com.developerphil.adbidea.adb.AdbFacade
import com.developerphil.adbidea.adb.AdbUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

class ClearDataAndRestartWithDebuggerAction : AdbAction() {
    override fun actionPerformed(e: AnActionEvent, project: Project) = AdbFacade.clearDataAndRestartWithDebugger(project)

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = AdbUtil.isDebuggingAvailable
    }
}
