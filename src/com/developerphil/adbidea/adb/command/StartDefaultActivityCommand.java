package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidUtils;

import java.util.ArrayList;
import java.util.List;
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
            StartActivityReceiver receiver = new StartActivityReceiver();
            device.executeShellCommand("am start " + component, receiver, 5L, TimeUnit.MINUTES);
            if (receiver.isSuccess()) {
                info(String.format("<b>%s</b> started on %s", packageName, device.getName()));
            } else {
                error(String.format("<b>%s</b> could not bet started on %s. \n\n<b>ADB Output:</b> \n%s", packageName, device.getName(), receiver.getMessage()));
            }
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

    public static class StartActivityReceiver extends MultiLineReceiver {

        public String message = "Nothing Received";

        public List<String> currentLines = new ArrayList<String>();

        @Override
        public void processNewLines(String[] strings) {
            for (String s : strings) {
                if (!Strings.isNullOrEmpty(s)) {
                    currentLines.add(s);
                }
            }
            computeMessage();
        }

        private void computeMessage() {
            message = Joiner.on("\n").join(currentLines);
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return currentLines.size() > 0 && currentLines.size() < 3;
        }

    }


}
