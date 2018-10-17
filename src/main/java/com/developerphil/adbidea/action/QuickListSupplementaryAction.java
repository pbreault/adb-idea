package com.developerphil.adbidea.action;

import com.intellij.ide.actions.QuickSwitchSchemeAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuickListSupplementaryAction extends QuickSwitchSchemeAction implements DumbAware {
    @Override
    protected void fillActions(@Nullable final Project project,
                               @NotNull final DefaultActionGroup group,
                               @NotNull final DataContext dataContext) {

        if (project == null) {
            return;
        }
        addAction("com.developerphil.adbidea.action.extend.ApplicationManagementPopupAction", group);
        addAction("com.developerphil.adbidea.action.extend.InteractingAction", group);
        addAction("com.developerphil.adbidea.action.extend.ShowDeviceInfoAction", group);
        addAction("com.developerphil.adbidea.action.extend.InstallApkAction", group);
        addAction("com.developerphil.adbidea.action.extend.PutStringAction", group);
        addAction("com.developerphil.adbidea.action.extend.ScreenRecordAction", group);
        addAction("com.developerphil.adbidea.action.extend.ScreenCaptureAction", group);
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
        return "ADB Supplementary Operations Popup";
    }

}
