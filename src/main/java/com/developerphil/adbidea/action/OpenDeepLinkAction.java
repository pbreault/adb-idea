package com.developerphil.adbidea.action;

import android.text.TextUtils;
import com.developerphil.adbidea.adb.AdbFacade;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;

public class OpenDeepLinkAction extends AdbAction {
    @Override
    public void actionPerformed(AnActionEvent e, Project project) {
        String deepLink = Messages.showInputDialog(project,"Insert the Deep Link you want to open",
                "ADB Open Deep Link", Messages.getInformationIcon(), "", new InputValidator() {
                    @Override
                    public boolean checkInput(String s) {
                        return !TextUtils.isEmpty(s) && s.contains("://");
                    }

                    @Override
                    public boolean canClose(String s) {
                        return true;
                    }
                });

        if (!TextUtils.isEmpty(deepLink)) {
            AdbFacade.openDeepLink(project, deepLink);
        }
    }
}
