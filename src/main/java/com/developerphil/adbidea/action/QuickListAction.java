package com.developerphil.adbidea.action;

import com.intellij.ide.actions.QuickSwitchSchemeAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.developerphil.adbidea.adb.AdbUtil.isDebuggingAvailable;

public class QuickListAction extends QuickSwitchSchemeAction implements DumbAware {
    protected void fillActions(@Nullable final Project project,
                               @NotNull final DefaultActionGroup group,
                               @NotNull final DataContext dataContext) {

        if (project == null) {
            return;
        }

        addAction("com.developerphil.adbidea.action.UninstallAction", group);
        addAction("com.developerphil.adbidea.action.KillAction", group);
        addAction("com.developerphil.adbidea.action.StartAction", group);
        addAction("com.developerphil.adbidea.action.RestartAction", group);
        addAction("com.developerphil.adbidea.action.ClearDataAction", group);
        addAction("com.developerphil.adbidea.action.ClearDataAndRestartAction", group);
        addAction("com.developerphil.adbidea.action.RevokePermissionsAction", group);
        addAction("com.developerphil.adbidea.action.UnlockPhoneAction", group);

        if (isDebuggingAvailable()) {
            group.addSeparator();

            addAction("com.developerphil.adbidea.action.StartWithDebuggerAction", group);
            addAction("com.developerphil.adbidea.action.RestartWithDebuggerAction", group);
        }

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
