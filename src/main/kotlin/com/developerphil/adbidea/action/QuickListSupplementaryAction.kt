package com.developerphil.adbidea.action

import com.intellij.ide.actions.QuickSwitchSchemeAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project

class QuickListSupplementaryAction : QuickSwitchSchemeAction(), DumbAware {
    override fun fillActions(project: Project?,
        group: DefaultActionGroup,
        dataContext: DataContext) {

        if (project == null) {
            return
        }
        addAction("com.developerphil.adbidea.action.extend.ApplicationManagementPopupAction", group)
        addAction("com.developerphil.adbidea.action.extend.InteractingAction", group)
        addAction("com.developerphil.adbidea.action.extend.ShowDeviceInfoAction", group)
        addAction("com.developerphil.adbidea.action.extend.InstallApkAction", group)
        addAction("com.developerphil.adbidea.action.extend.PutStringAction", group)
        addAction("com.developerphil.adbidea.action.extend.ScreenRecordAction", group)
        addAction("com.developerphil.adbidea.action.extend.ScreenCaptureAction", group)
    }

    override fun isEnabled(): Boolean {
        return true
    }

    private fun addAction(actionId: String, toGroup: DefaultActionGroup) {
        val action = ActionManager.getInstance().getAction(actionId)

        // add action to group if it is available
        if (action != null) {
            toGroup.add(action)
        }
    }

    override fun getPopupTitle(e: AnActionEvent): String {
        return "ADB Supplementary Operations Popup"
    }

}
