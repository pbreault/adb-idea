package com.draekko.adbtools.action;

import com.intellij.ide.actions.QuickSwitchSchemeAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuickListAction extends QuickSwitchSchemeAction implements DumbAware {

    protected void fillActions(@Nullable final Project project,
                               @NotNull final DefaultActionGroup group,
                               @NotNull final DataContext dataContext) {

        if (project == null) {
            return;
        }

        addAction("adbtools.action.UninstallAction", group);
        addAction("adbtools.action.KillAction", group);
        addAction("adbtools.action.StartAction", group);
        addAction("adbtools.action.StartWithDebuggerAction", group);
        addAction("adbtools.action.RestartAction", group);
        addAction("adbtools.action.RestartWithDebuggerAction", group);
        addAction("adbtools.action.ClearDataAction", group);
        addAction("adbtools.action.ClearDataAndRestartAction", group);
        addAction("adbtools.action.ClearDataAndRestartWithDebugerAction", group);
    }

    protected boolean isEnabled() {
        return true;
    }

    private void addAction(final String actionId, final DefaultActionGroup toGroup) {
        final AnAction action = ActionManager.getInstance().getAction(actionId);

        // add action to group if it is available
        if (action != null) {
            toGroup.add(action);
        }
    }

    protected String getPopupTitle(AnActionEvent e) {
        return "ADB Operations Popup";
    }

}
