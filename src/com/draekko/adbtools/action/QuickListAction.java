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

        addAction("com.draekko.adbtools.action.UninstallAction", group);
        addAction("com.draekko.adbtools.action.KillAction", group);
        addAction("com.draekko.adbtools.action.StartAction", group);
        addAction("com.draekko.adbtools.action.RestartAction", group);
        addAction("com.draekko.adbtools.action.ClearDataAction", group);
        addAction("com.draekko.adbtools.action.ClearDataAndRestartAction", group);
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
