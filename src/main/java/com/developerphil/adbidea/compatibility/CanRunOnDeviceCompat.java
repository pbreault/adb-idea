package com.developerphil.adbidea.compatibility;

import com.android.ddmlib.IDevice;
import com.android.sdklib.AndroidVersion;
import com.android.sdklib.IAndroidTarget;
import com.android.tools.idea.run.*;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.EnumSet;

public class CanRunOnDeviceCompat extends BackwardCompatibleGetter<LaunchCompatibility> {

    private AndroidFacet myFacet;
    private final AndroidVersion myMinSdkVersion;
    private final IAndroidTarget myProjectTarget;
    private final EnumSet<IDevice.HardwareFeature> myRequiredHardwareFeatures;
    private final IDevice device;

    public CanRunOnDeviceCompat(AndroidFacet myFacet, AndroidVersion myMinSdkVersion, IAndroidTarget myProjectTarget, EnumSet<IDevice.HardwareFeature> myRequiredHardwareFeatures, IDevice device) {
        this.myFacet = myFacet;
        this.myMinSdkVersion = myMinSdkVersion;
        this.myProjectTarget = myProjectTarget;
        this.myRequiredHardwareFeatures = myRequiredHardwareFeatures;
        this.device = device;
    }

    @Override
    // Android Studio 2.2 RC
    protected LaunchCompatibility getCurrentImplementation() throws Throwable {
        AndroidDevice androidDevice = new ConnectedAndroidDevice(device, null);
        return LaunchCompatibilityCheckerImpl.create(myFacet).validate(androidDevice);
    }

    @Override
    // Intellij 2016.1 / 2016.2
    protected LaunchCompatibility getPreviousImplementation() {
        return new CanRunOnDeviceCompatForIntellij2016DotOneAndDotTwo(myMinSdkVersion, myProjectTarget, myRequiredHardwareFeatures, device).get();
    }
}
