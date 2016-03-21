package com.developerphil.adbidea.compatibility;

import com.android.tools.idea.run.activity.DefaultActivityLocator;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;
import org.joor.Reflect;

public class GetDefaultLauncherActivityNameCompatBefore2_1 extends BackwardCompatibleGetter<String> {

    private final AndroidFacet facet;

    public GetDefaultLauncherActivityNameCompatBefore2_1(AndroidFacet facet) {
        this.facet = facet;
    }

    @Override
    // Android studio 2.0 preview 4
    protected String getCurrentImplementation() throws Throwable {
        return Reflect.on(DefaultActivityLocator.class).call("getDefaultLauncherActivityName", facet.getManifest()).get();
    }

    @Override
    // Intellij 15.0.2
    protected String getPreviousImplementation() {
        return Reflect.on(AndroidUtils.class).call("getDefaultLauncherActivityName", facet.getManifest()).get();
    }
}
