package com.developerphil.adbidea.compatibility;

import com.android.ddmlib.IDevice;
import com.android.sdklib.AndroidVersion;
import com.android.sdklib.IAndroidTarget;
import com.android.tools.idea.run.AndroidDevice;
import com.android.tools.idea.run.LaunchCompatibility;
import org.joor.Reflect;

import java.util.EnumSet;

// Up until android studio 2.0 preview 8
public class CanRunOnDeviceCompatBefore2_0 extends BackwardCompatibleGetter<LaunchCompatibility> {

    private final AndroidVersion myMinSdkVersion;
    private final IAndroidTarget myProjectTarget;
    private final EnumSet<IDevice.HardwareFeature> myRequiredHardwareFeatures;
    private final IDevice device;

    public CanRunOnDeviceCompatBefore2_0(AndroidVersion myMinSdkVersion, IAndroidTarget myProjectTarget, EnumSet<IDevice.HardwareFeature> myRequiredHardwareFeatures, IDevice device) {
        this.myMinSdkVersion = myMinSdkVersion;
        this.myProjectTarget = myProjectTarget;
        this.myRequiredHardwareFeatures = myRequiredHardwareFeatures;
        this.device = device;
    }

    @Override
    // Android studio 1.5+
    protected LaunchCompatibility getCurrentImplementation() throws Throwable {
        AndroidDevice androidDevice = Reflect.on("com.android.tools.idea.run.ConnectedAndroidDevice").create(device, null).get();
        return Reflect.on(LaunchCompatibility.class).call("canRunOnDevice", myMinSdkVersion, myProjectTarget, myRequiredHardwareFeatures, androidDevice, null).get();
    }

    @Override
    // Android Studio 1.4-
    protected LaunchCompatibility getPreviousImplementation() {
        return Reflect.on(LaunchCompatibility.class).call("canRunOnDevice", myMinSdkVersion, myProjectTarget, myRequiredHardwareFeatures, device, null).get();
    }
}
