package com.developerphil.adbidea.adb;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.tools.idea.model.ManifestInfo;
import com.android.tools.idea.run.activity.DefaultActivityLocator;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import org.jetbrains.android.dom.AndroidAttributeValue;
import org.jetbrains.android.dom.manifest.UsesFeature;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;
import org.joor.Reflect;
import org.joor.ReflectException;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdbUtil {

    public static boolean isAppInstalled(IDevice device, String packageName) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        GenericReceiver receiver = new GenericReceiver();
        // "pm list packages com.my.package" will return one line per package installed that corresponds to this package.
        // if this list is empty, we know for sure that the app is not installed
        device.executeShellCommand("pm list packages " + packageName, receiver, 15L, TimeUnit.SECONDS);

        //TODO make sure that it is the exact package name and not a subset.
        // e.g. if our app is called com.example but there is another app called com.example.another.app, it will match and return a false positive
        return !receiver.getAdbOutputLines().isEmpty();
    }


    /**
     * Computes the project's package while preserving backward compatibility between android studio 0.4.3 and 0.4.4
     */
    public static String computePackageName(AndroidFacet facet) {
        try {
            Object androidModuleInfo = facet.getClass().getMethod("getAndroidModuleInfo").invoke(facet);
            return (String) androidModuleInfo.getClass().getMethod("getPackage").invoke(androidModuleInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getDefaultLauncherActivityName(AndroidFacet facet) {
        try {
            // Studio 1.5
            return DefaultActivityLocator.getDefaultLauncherActivityName(facet.getManifest());
        } catch (Error e) {
            try {
                // Studio 1.4
                return Reflect.on("org.jetbrains.android.run.DefaultActivityLocator").call("getDefaultLauncherActivityName", facet.getManifest()).get();
            } catch (ReflectException exception) {
                // Studio 1.3 && Intellij 14.1
                return Reflect.on(AndroidUtils.class).call("getDefaultLauncherActivityName", facet.getManifest()).get();
            }
        }
    }

    // compatibility between studio 1.4 and 1.5-Preview1
    public static EnumSet<IDevice.HardwareFeature> getRequiredHardwareFeatures(AndroidFacet facet) {
        try {
            if (com.android.tools.idea.run.LaunchUtils.isWatchFeatureRequired(facet)) {
                return EnumSet.of(IDevice.HardwareFeature.WATCH);
            } else {
                return EnumSet.noneOf(IDevice.HardwareFeature.class);
            }
        } catch (Error e) {
            ManifestInfo manifestInfo = ManifestInfo.get(facet.getModule(), true);
            List<UsesFeature> requiredFeatures = Reflect.on(manifestInfo).call("getRequiredFeatures").get();
            return getRequiredHardwareFeatures(requiredFeatures);
        }
    }

    // Studio 1.4 and below
    private static EnumSet<IDevice.HardwareFeature> getRequiredHardwareFeatures(List<UsesFeature> requiredFeatures) {
        // Currently, this method is hardcoded to only search if the list of required features includes a watch.
        // We may not want to search the device for every possible feature, but only a small subset of important
        // features, starting with hardware type watch..

        for (UsesFeature feature : requiredFeatures) {
            AndroidAttributeValue<String> name = feature.getName();
            if (name != null && UsesFeature.HARDWARE_TYPE_WATCH.equals(name.getStringValue())) {
                return EnumSet.of(IDevice.HardwareFeature.WATCH);
            }
        }

        return EnumSet.noneOf(IDevice.HardwareFeature.class);
    }
}
