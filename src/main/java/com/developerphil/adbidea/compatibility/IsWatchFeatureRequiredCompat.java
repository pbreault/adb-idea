package com.developerphil.adbidea.compatibility;

import org.jetbrains.android.facet.AndroidFacet;
import org.joor.Reflect;

import static com.android.tools.idea.run.util.LaunchUtils.isWatchFeatureRequired;

public class IsWatchFeatureRequiredCompat extends BackwardCompatibleGetter<Boolean> {

    private AndroidFacet facet;

    public IsWatchFeatureRequiredCompat(AndroidFacet facet) {
        this.facet = facet;
    }

    @Override
    // Android studio 2.0-Preview5
    protected Boolean getCurrentImplementation() throws Throwable {
        return isWatchFeatureRequired(facet);
    }

    @Override
    // Android studio 2.0
    protected Boolean getPreviousImplementation() {
        return Reflect.on("com.android.tools.idea.run.LaunchUtils").call("isWatchFeatureRequired", facet).get();
    }

}
