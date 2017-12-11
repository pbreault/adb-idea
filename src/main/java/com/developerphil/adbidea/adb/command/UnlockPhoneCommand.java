package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

/**
 * Created by nsaiisasidhar on 12/11/2017.
 */
public class UnlockPhoneCommand extends ShellCommand {
    @Override
    public String getCommandLine() {
        return getEvent("82");
    }

}
