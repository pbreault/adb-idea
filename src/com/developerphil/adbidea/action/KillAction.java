package com.developerphil.adbidea.action;

import com.developerphil.adbidea.adb.AdbFacade;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

/**
 * Created by pbreault on 9/28/13.
 */
public class KillAction extends AdbAction {

    public void actionPerformed(AnActionEvent e, Project project) {
        AdbFacade.kill(project);
    }

}
