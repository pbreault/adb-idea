package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

public interface Command {
    void run(Project project, IDevice device, AndroidFacet facet, String packageName);
}
