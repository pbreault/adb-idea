package com.developerphil.adbidea.adb.command;

import android.text.TextUtils;
import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.ui.NotificationHelper.error;

public class OpenDeepLinkCommand implements Command {
    private String deepLink;

    public OpenDeepLinkCommand(String deepLink) {
        this.deepLink = deepLink;
    }

    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            if (TextUtils.isEmpty(deepLink)) {
                error("Deep Link was empty...");
                return false;
            }

            device.executeShellCommand("am start -a android.intent.action.VIEW -d \"" + deepLink + "\"",
                    new GenericReceiver(), 15L, TimeUnit.SECONDS);

            return true;
        } catch (Exception e1) {
            error("Opening Deep Link failed... " + e1.getMessage());
        }
        return false;
    }
}
