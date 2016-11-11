package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.adb.AdbUtil.isAppInstalled;
import static com.developerphil.adbidea.ui.NotificationHelper.*;

/**
 * Created by fmatos on 1/05/2016.
 */
public abstract class ShellCommand implements Command {

    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {

        GenericReceiver receiver = new GenericReceiver();

        try {
            if (isAppInstalled(device, packageName)) {
                String commandLine = getCommandLine();
                device.executeShellCommand(commandLine, receiver, 15L, TimeUnit.SECONDS);
                info(String.format("<b>%s</b> %s --> %s", packageName, device.getName(),commandLine));

                if ( ! receiver.isSuccess() ) {
                    error("Shell error " + receiver.getAdbOutputLines());
                    return false;
                }
                return true;
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.getName()));
            }
        } catch (Exception e1) {
            error("Custom message 2... " + e1.getMessage());
        }
        return false;
    }


    public String getEvent(String event) {
        return " input keyevent " + event;
    }

    public abstract String getCommandLine();
}
