package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.android.tools.idea.run.activity.ActivityLocator;
import com.android.tools.idea.run.activity.DefaultActivityLocator;
import com.developerphil.adbidea.adb.ShellCommandsFactory;
import com.developerphil.adbidea.debugger.Debugger;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class StartDefaultActivityCommand implements Command {

    private final boolean withDebugger;

    public StartDefaultActivityCommand(boolean withDebugger) {
        this.withDebugger = withDebugger;
    }

    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            String activityName = getDefaultActivityName(facet, device);
            StartActivityReceiver receiver = new StartActivityReceiver();
            String shellCommand = ShellCommandsFactory.startActivity(packageName, activityName, withDebugger);

            device.executeShellCommand(shellCommand, receiver, 15L, TimeUnit.SECONDS);

            if (withDebugger) {
                new Debugger(project, device, packageName).attach();
            }

            if (receiver.isSuccess()) {
                info(String.format("<b>%s</b> started on %s", packageName, device.getName()));
                return true;
            } else {
                error(String.format("<b>%s</b> could not bet started on %s. \n\n<b>ADB Output:</b> \n%s", packageName, device.getName(), receiver.getMessage()));
            }
        } catch (Exception e) {
            error("Start fail... " + e.getMessage());
        }

        return false;
    }

    private String getDefaultActivityName(final AndroidFacet facet, final IDevice device) throws ActivityLocator.ActivityLocatorException {
        return ApplicationManager.getApplication()
                .runReadAction((ThrowableComputable<String, ActivityLocator.ActivityLocatorException>)
                        () -> new DefaultActivityLocator(facet).getQualifiedActivityName(device));
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
