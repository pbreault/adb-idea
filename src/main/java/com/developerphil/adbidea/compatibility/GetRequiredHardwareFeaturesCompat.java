package com.developerphil.adbidea.compatibility;

import com.android.ddmlib.IDevice;
import com.android.tools.idea.model.ManifestInfo;
import org.jetbrains.android.dom.AndroidAttributeValue;
import org.jetbrains.android.dom.manifest.UsesFeature;
import org.jetbrains.android.facet.AndroidFacet;
import org.joor.Reflect;

import java.util.EnumSet;
import java.util.List;

public class GetRequiredHardwareFeaturesCompat extends BackwardCompatibleGetter<EnumSet<IDevice.HardwareFeature>> {

    private AndroidFacet facet;

    public GetRequiredHardwareFeaturesCompat(AndroidFacet facet) {
        this.facet = facet;
    }

    @Override
    // Android studio 1.5 - 2.0-Preview5
    protected EnumSet<IDevice.HardwareFeature> getCurrentImplementation() throws Throwable {
        if (new IsWatchFeatureRequiredCompat(facet).get()) {
            return EnumSet.of(IDevice.HardwareFeature.WATCH);
        } else {
            return EnumSet.noneOf(IDevice.HardwareFeature.class);
        }
    }

    @Override
    // Android studio 1.4 and below
    protected EnumSet<IDevice.HardwareFeature> getPreviousImplementation() {
        ManifestInfo manifestInfo = ManifestInfo.get(facet.getModule(), true);
        List<UsesFeature> requiredFeatures = Reflect.on(manifestInfo).call("getRequiredFeatures").get();

        for (UsesFeature feature : requiredFeatures) {
            AndroidAttributeValue<String> name = feature.getName();
            if (name != null && UsesFeature.HARDWARE_TYPE_WATCH.equals(name.getStringValue())) {
                return EnumSet.of(IDevice.HardwareFeature.WATCH);
            }
        }

        return EnumSet.noneOf(IDevice.HardwareFeature.class);
    }

}
