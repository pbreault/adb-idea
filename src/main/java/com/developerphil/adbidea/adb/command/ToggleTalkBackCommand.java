package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class ToggleTalkBackCommand implements Command {
    private static final String TALK_BACK_OFF_SETTING = "com.android.talkback/com.google.android.marvin.talkback.TalkBackService";
    private static final String TALK_BACK_ON_SETTING = "com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService";

    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            ToggleTalkBackReceiver receiver = new ToggleTalkBackReceiver();
            device.executeShellCommand("settings get secure enabled_accessibility_services", receiver, 15L, TimeUnit.SECONDS);

            // Sometimes it might happen that the current setting is empty but we can still try to turn TalkBack on
            if (TALK_BACK_OFF_SETTING.equals(receiver.getMessage()) || receiver.getMessage().isEmpty()) {
                device.executeShellCommand("settings put secure enabled_accessibility_services " +
                        TALK_BACK_ON_SETTING, new GenericReceiver(), 15L, TimeUnit.SECONDS);
                info(String.format("TalkBack turned on on %s", device.getName()));
            } else if (TALK_BACK_ON_SETTING.equals(receiver.getMessage())) {
                device.executeShellCommand("settings put secure enabled_accessibility_services " +
                        TALK_BACK_OFF_SETTING, new GenericReceiver(), 15L, TimeUnit.SECONDS);
                info(String.format("TalkBack turned off on %s", device.getName()));
            } else {
                error("TalkBack toggle failed due to unknown current setting: " + receiver.getMessage());
                return false;
            }

            return true;
        } catch (Exception e1) {
            error("TalkBack toggle failed... " + e1.getMessage());
        }
        return false;
    }

    private class ToggleTalkBackReceiver extends MultiLineReceiver {

        private String message = "";
        private List<String> currentLines = new ArrayList<>();

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

        String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return currentLines.size() > 0 && currentLines.size() < 3;
        }
    }
}
