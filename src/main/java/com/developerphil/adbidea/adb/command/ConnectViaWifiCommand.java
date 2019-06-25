package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.ConsoleUtil;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.intellij.openapi.project.Project;
import javafx.util.Pair;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class ConnectViaWifiCommand implements Command {
    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            if (device.isEmulator()) {
                error(String.format("Don't connecting to emulator %s", device.getName()));
                return true;
            }

            GenericReceiver receiver = new GenericReceiver();
            device.executeShellCommand("ip route", receiver, 5L, TimeUnit.SECONDS);
            List<String> results = receiver.getAdbOutputLines();
            String deviceIp = results.get(0).substring(results.get(0).lastIndexOf(" "));

            Pair<Integer, ArrayList<String>> enableResult = ConsoleUtil.exec(String.format("adb -s %s tcpip 5555", device.getSerialNumber()).split(" "));

            if (enableResult == null || enableResult.getKey() != 0) {
                error(String.format("Connect to device %s fail...", device.getName()));
                return false;
            }

            System.out.println(results);

            Pair<Integer, ArrayList<String>> connectResult = ConsoleUtil.exec(String.format("adb -s %s connect %s:5555", device.getSerialNumber(), deviceIp).split(" "));


            if (connectResult != null && connectResult.getKey() == 0 && connectResult.getValue().get(0).contains("connected")) {
                info(String.format("Device %s connected to %s:5555", device.getName(), deviceIp));
                return true;
            } else {
                error(String.format("Connect to device %s fail...", device.getName()));
                return false;
            }
        } catch (Exception e1) {
            error(String.format("Connect to device %s fail... %s", device.getName(), e1.getMessage()));
        }
        return false;
    }
}
