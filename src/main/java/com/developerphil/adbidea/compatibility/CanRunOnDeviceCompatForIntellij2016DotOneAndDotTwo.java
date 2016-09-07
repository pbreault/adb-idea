package com.developerphil.adbidea.compatibility;

import com.android.ddmlib.IDevice;
import com.android.sdklib.AndroidVersion;
import com.android.sdklib.IAndroidTarget;
import com.android.tools.idea.run.AndroidDevice;
import com.android.tools.idea.run.LaunchCompatibility;
import org.joor.Reflect;

import java.util.EnumSet;

// LaunchCompatibility#canRunOnDevice() has a slightly different method signature between 2016.1 and 2016.2
public class CanRunOnDeviceCompatForIntellij2016DotOneAndDotTwo extends BackwardCompatibleGetter<LaunchCompatibility> {

    private final AndroidVersion myMinSdkVersion;
    private final IAndroidTarget myProjectTarget;
    private final EnumSet<IDevice.HardwareFeature> myRequiredHardwareFeatures;
    private final IDevice device;

    public CanRunOnDeviceCompatForIntellij2016DotOneAndDotTwo(AndroidVersion myMinSdkVersion, IAndroidTarget myProjectTarget, EnumSet<IDevice.HardwareFeature> myRequiredHardwareFeatures, IDevice device) {
        this.myMinSdkVersion = myMinSdkVersion;
        this.myProjectTarget = myProjectTarget;
        this.myRequiredHardwareFeatures = myRequiredHardwareFeatures;
        this.device = device;
    }

    @Override
    // Intellij 2016.2
    protected LaunchCompatibility getCurrentImplementation() throws Throwable {
        return Reflect.on(LaunchCompatibility.class).call("canRunOnDevice", myMinSdkVersion, myProjectTarget, myRequiredHardwareFeatures, androidDevice()).get();
    }

    @Override
    // Intellij 2016.1
    protected LaunchCompatibility getPreviousImplementation() {
        return Reflect.on(LaunchCompatibility.class).call("canRunOnDevice", myMinSdkVersion, myProjectTarget, myRequiredHardwareFeatures, androidDevice(), myProjectTarget).get();
    }

    private AndroidDevice androidDevice() {
        return Reflect.on("com.android.tools.idea.run.ConnectedAndroidDevice").create(device, null).get();
    }
}
