package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.adb.AdbUtil.isAppInstalled;
import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class RevokePermissionsCommand implements Command {
    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            if (deviceHasMarshmallow(device))
                if (isAppInstalled(device, packageName)) {
                    GenericReceiver shellOutputReceiver = new GenericReceiver();
                    device.executeShellCommand("dumpsys package " + packageName, shellOutputReceiver, 15L, TimeUnit.SECONDS);
                    shellOutputReceiver.getAdbOutputLines().stream()
                            //only granted permissions, they come in "android.permission.CAMERA: granted=true"
                            .filter(s -> s.contains("permission")).filter(s -> s.contains("granted=true"))
                            //just the permission name is important
                            .map(s -> s.split(":")[0].trim())
                            .forEach(s -> {
                                try {
                                    device.executeShellCommand("pm revoke " + packageName + " " + s, new GenericReceiver(), 15L, TimeUnit.SECONDS);
                                    info(String.format("Permission <b>%s</b> revoked on %s", s, device.getName()));
                                } catch (Exception e) {
                                    error(String.format("Revoking %s failed on %s: %s", s, device.getName(), e.getMessage()));
                                }
                            });
                    return true;
                } else {
                    error(String.format("<b>%s</b> is not installed on %s", packageName, device.getName()));
                }
            else {
                error(String.format("%s must be at least api level 23", device.getName()));
            }
        } catch (Exception e1) {
            error("Revoking permissions fail... " + e1.getMessage());
        }
        return false;
    }

    private boolean deviceHasMarshmallow(IDevice device) {
        return device.getVersion().getApiLevel() >= 23;
    }
}
