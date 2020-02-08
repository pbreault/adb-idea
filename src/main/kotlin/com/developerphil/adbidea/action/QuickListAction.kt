package com.developerphil.adbidea.action

import com.developerphil.adbidea.adb.AdbUtil
import com.intellij.ide.actions.QuickSwitchSchemeAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project

class QuickListAction : QuickSwitchSchemeAction(), DumbAware {
    override fun fillActions(project: Project?, group: DefaultActionGroup, dataContext: DataContext) {

        if (project == null) {
            return
        }

        addAction("com.developerphil.adbidea.action.UninstallAction", group)
        addAction("com.developerphil.adbidea.action.KillAction", group)
        addAction("com.developerphil.adbidea.action.StartAction", group)
        addAction("com.developerphil.adbidea.action.RestartAction", group)
        addAction("com.developerphil.adbidea.action.ClearDataAction", group)
        addAction("com.developerphil.adbidea.action.ClearDataAndRestartAction", group)
        addAction("com.developerphil.adbidea.action.RevokePermissionsAction", group)
        if (AdbUtil.isDebuggingAvailable) {
            group.addSeparator()
            addAction("com.developerphil.adbidea.action.StartWithDebuggerAction", group)
            addAction("com.developerphil.adbidea.action.RestartWithDebuggerAction", group)
        }
    }


    private fun addAction(actionId: String, toGroup: DefaultActionGroup) {
        // add action to group if it is available
        ActionManager.getInstance().getAction(actionId)?.let {
            toGroup.add(it)
        }
    }

    override fun isEnabled() = true
    override fun getPopupTitle(e: AnActionEvent) = "ADB Operations Popup"
}