package io.github.raghavsatyadev.adbidea.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import io.github.raghavsatyadev.adbidea.adb.AdbFacade

class ClearDataAndRestartWithDebuggerAction : AdbAction() {
    override fun actionPerformed(e: AnActionEvent, project: Project) =
        AdbFacade.clearDataAndRestartWithDebugger(project)
}
