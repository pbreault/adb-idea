package com.developerphil.adbidea.compatibility;

import com.android.tools.idea.run.activity.DefaultActivityLocator;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

public class GetDefaultLauncherActivityNameCompat extends BackwardCompatibleGetter<String> {

    private final Project project;
    private final AndroidFacet facet;

    public GetDefaultLauncherActivityNameCompat(Project project, AndroidFacet facet) {
        this.project = project;
        this.facet = facet;
    }

    @Override
    // Android Studio 2.1 Preview 1 +
    protected String getCurrentImplementation() throws Throwable {
        return DefaultActivityLocator.getDefaultLauncherActivityName(project, facet.getManifest());
    }

    @Override
    // Android Studio 2.0
    protected String getPreviousImplementation() {
        return new GetDefaultLauncherActivityNameCompatBefore2_1(facet).get();
    }
}
