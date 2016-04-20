package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

public interface Command {
    /**
     *
     * @return true if the command executed properly
     */
    boolean run(Project project, IDevice device, AndroidFacet facet, String packageName);
}
