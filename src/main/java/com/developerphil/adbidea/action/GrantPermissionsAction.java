package com.developerphil.adbidea.action;

import com.developerphil.adbidea.adb.AdbFacade;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

/**
 * Created by Stephan Hagios on 28.07.17.
 */
public class GrantPermissionsAction extends AdbAction {
    @Override
    public void actionPerformed(AnActionEvent e, Project project) {
        AdbFacade.grantPermissions(project);
    }
}
