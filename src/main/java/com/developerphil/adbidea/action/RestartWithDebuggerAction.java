package com.developerphil.adbidea.action;

import com.developerphil.adbidea.adb.AdbFacade;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class RestartWithDebuggerAction extends AdbAction {

    public void actionPerformed(AnActionEvent e, Project project) {
        AdbFacade.restartDefaultActivityWithDebugger(project);
    }
}
