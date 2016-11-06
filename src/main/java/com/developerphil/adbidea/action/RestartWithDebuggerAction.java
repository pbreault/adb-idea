package com.developerphil.adbidea.action;

import com.developerphil.adbidea.adb.AdbFacade;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import static com.developerphil.adbidea.adb.AdbUtil.isDebuggingAvailable;

public class RestartWithDebuggerAction extends AdbAction {

    public void actionPerformed(AnActionEvent e, Project project) {
        AdbFacade.restartDefaultActivityWithDebugger(project);
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(isDebuggingAvailable());
    }

}
