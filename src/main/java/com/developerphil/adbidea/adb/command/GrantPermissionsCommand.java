package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.adb.AdbUtil.isAppInstalled;
import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

/**
 * Created by Stephan Hagios on 28.07.17.
 */
public class GrantPermissionsCommand implements Command {
    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            if (deviceHasMarshmallow(device))
                if (isAppInstalled(device, packageName)) {
                    GenericReceiver shellOutputReceiver = new GenericReceiver();
                    device.executeShellCommand("dumpsys package " + packageName, shellOutputReceiver, 15L, TimeUnit.SECONDS);
                    List<String> adbOutputLines = getRequestedPermissions(shellOutputReceiver.getAdbOutputLines());
                    info(Arrays.toString(adbOutputLines.toArray()));
                    adbOutputLines.stream()
                            .map(s -> s.split(":")[0].trim())
                            .forEach(s -> {
                                try {
                                    device.executeShellCommand("pm grant " + packageName + " " + s, new GenericReceiver(), 15L, TimeUnit.SECONDS);
                                    info(String.format("Permission <b>%s</b> granted on %s", s, device.getName()));
                                } catch (Exception e) {
                                    error(String.format("Granting %s failed on %s: %s", s, device.getName(), e.getMessage()));
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
            error("Granting permissions fail... " + e1.getMessage());
        }
        return false;
    }

    private boolean deviceHasMarshmallow(IDevice device) {
        return device.getVersion().getApiLevel() >= 23;
    }

    private List<String> getRequestedPermissions(List<String> list) {
        boolean shouldRecord = false;
        List<String> requestPermissions = new ArrayList<>();
        for (String s : list) {
            if (!s.contains(".permission.")) {
                shouldRecord = false;
            }
            if (s.contains("requested permissions:")) {
                shouldRecord = true;
                continue;
            }

            if (shouldRecord) {
                String permissionName = s.replace(";", "").trim();
                requestPermissions.add(permissionName);
            }
        }
        return requestPermissions;
    }
}
