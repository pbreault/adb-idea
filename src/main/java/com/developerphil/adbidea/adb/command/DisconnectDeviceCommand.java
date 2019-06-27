package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.AdbUtil;
import com.developerphil.adbidea.adb.ConsoleUtil;
import com.intellij.openapi.project.Project;
import javafx.util.Pair;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.ArrayList;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class DisconnectDeviceCommand implements Command {
    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            Pair<Integer, ArrayList<String>> connectResult = ConsoleUtil.exec(String.format(AdbUtil.getAdbPath() + " -s %s disconnect ", device.getSerialNumber()).split(" "));

            if (connectResult != null && connectResult.getKey() == 0 && connectResult.getValue().get(0).contains("disconnected")) {
                info(String.format("Device %s disconnected", device.getName()));
                return true;
            } else {
                error(String.format("Disconnected device %s fail...", device.getName()));
                return false;
            }
        } catch (Exception e1) {
            error(String.format("Disconnected device %s fail... %s", device.getName(), e1.getMessage()));
        }
        return false;
    }
}
