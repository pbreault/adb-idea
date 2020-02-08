package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class StartActivityCommand implements Command {
    private String action;

    public StartActivityCommand(@Nonnull String action) {
        this.action = action;
    }

    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            GenericReceiver genericReceiver = new GenericReceiver();
            device.executeShellCommand("am start -a " + action, genericReceiver, 15L, TimeUnit.SECONDS);
            if(genericReceiver.isSuccess()) {
                info(String.format("<b>%s</b> started on %s", action, device.getName()));
                return true;
            }
            error("Error: " + genericReceiver.getAdbOutputString());
        } catch (Exception e1) {
            error("Start fail... \n" + e1.getMessage());
        }
        return false;
    }
}
