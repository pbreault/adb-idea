package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.adb.GenericReceiver;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

/**
 * Created by pbreault on 1/2/14.
 */
public class StartDefaultActivityCommand implements Command {
    @Override
    public void run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        String defaultActivityName = getDefaultActivityName(facet);
        String component = packageName + "/" + defaultActivityName;

        try {
            device.executeShellCommand("am start " + component, new GenericReceiver(), 5L, TimeUnit.MINUTES);
            info(String.format("<b>%s</b> started app on %s", packageName, device.getName()));
        } catch (Exception e1) {
            error("Start fail... " + e1.getMessage());
            e1.printStackTrace();
        }
    }

    private String getDefaultActivityName(final AndroidFacet facet) {
        return ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return AndroidUtils.getDefaultActivityName(facet.getManifest());
            }
        });
    }
}
