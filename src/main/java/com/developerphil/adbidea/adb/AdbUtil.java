package com.developerphil.adbidea.adb;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import org.joor.Reflect;

import java.io.IOException;
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

    // The android debugger class is not available in Intellij 2016.1.
    // Nobody should use that version but it's still the minimum "supported" version since android studio 2.2
    // shares the same base version.
    public static Boolean isDebuggingAvailable() {
        try {
            Reflect.on("com.android.tools.idea.run.editor.AndroidDebugger").get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
