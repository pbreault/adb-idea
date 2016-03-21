package com.developerphil.adbidea.compatibility;

import com.android.ddmlib.IDevice;
import com.android.sdklib.AndroidVersion;
import com.android.sdklib.IAndroidTarget;
import com.android.tools.idea.run.AndroidDevice;
import com.android.tools.idea.run.LaunchCompatibility;
import org.joor.Reflect;

import java.util.EnumSet;

public class CanRunOnDeviceCompat extends BackwardCompatibleGetter<LaunchCompatibility> {

    private final AndroidVersion myMinSdkVersion;
    private final IAndroidTarget myProjectTarget;
    private final EnumSet<IDevice.HardwareFeature> myRequiredHardwareFeatures;
    private final IDevice device;

    public CanRunOnDeviceCompat(AndroidVersion myMinSdkVersion, IAndroidTarget myProjectTarget, EnumSet<IDevice.HardwareFeature> myRequiredHardwareFeatures, IDevice device) {
        this.myMinSdkVersion = myMinSdkVersion;
        this.myProjectTarget = myProjectTarget;
        this.myRequiredHardwareFeatures = myRequiredHardwareFeatures;
        this.device = device;
    }

    @Override
    // Android Studio 2.0 Preview 8 +
    protected LaunchCompatibility getCurrentImplementation() throws Throwable {
        AndroidDevice androidDevice = Reflect.on("com.android.tools.idea.run.ConnectedAndroidDevice").create(device, null).get();
        return LaunchCompatibility.canRunOnDevice(myMinSdkVersion, myProjectTarget, myRequiredHardwareFeatures, androidDevice);
    }

    @Override
    // Android Studio 2.0 Preview 7
    protected LaunchCompatibility getPreviousImplementation() {
        return new CanRunOnDeviceCompatBefore2_0(myMinSdkVersion, myProjectTarget, myRequiredHardwareFeatures, device).get();
    }
}
