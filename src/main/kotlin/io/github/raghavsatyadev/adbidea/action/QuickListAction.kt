package io.github.raghavsatyadev.adbidea.action

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

    addAction("io.github.raghavsatyadev.adbidea.action.UninstallAction", group)
    addAction("io.github.raghavsatyadev.adbidea.action.KillAction", group)
    addAction("io.github.raghavsatyadev.adbidea.action.StartAction", group)
    addAction("io.github.raghavsatyadev.adbidea.action.RestartAction", group)
    addAction("io.github.raghavsatyadev.adbidea.action.ClearDataAction", group)
    addAction("io.github.raghavsatyadev.adbidea.action.ClearDataAndRestartAction", group)
    addAction("io.github.raghavsatyadev.adbidea.action.RevokePermissionsAction", group)
    group.addSeparator()
    addAction("io.github.raghavsatyadev.adbidea.action.StartWithDebuggerAction", group)
    addAction("io.github.raghavsatyadev.adbidea.action.RestartWithDebuggerAction", group)
  }

  private fun addAction(actionId: String, toGroup: DefaultActionGroup) {
    // add action to group if it is available
    ActionManager.getInstance().getAction(actionId)?.let { toGroup.add(it) }
  }

  override fun isEnabled() = true

  override fun getPopupTitle(e: AnActionEvent) = "ADB Operations Popup"
}
