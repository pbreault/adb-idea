package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

/**
 * Created by fmatos on 1/05/2016.
 */
public class PressPowerCommand extends ShellCommand {
    @Override
    public String getCommandLine() {
        return getEvent("26");
    }

}
