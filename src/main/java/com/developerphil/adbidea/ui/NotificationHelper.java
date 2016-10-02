package com.developerphil.adbidea.ui;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;

public class NotificationHelper {

    private static final NotificationGroup INFO = NotificationGroup.logOnlyGroup("ADB Idea (Logging)");
    private static final NotificationGroup ERRORS = NotificationGroup.balloonGroup("ADB Idea (Errors)");
    private static final NotificationListener NOOP_LISTENER = (notification, event) -> {
    };

    public static void info(String message) {
        sendNotification(message, NotificationType.INFORMATION, INFO);
    }

    public static void error(String message) {
        sendNotification(message, NotificationType.ERROR, ERRORS);
    }

    private static void sendNotification(String message, NotificationType notificationType, NotificationGroup notificationGroup) {
        notificationGroup.createNotification("ADB IDEA", escapeString(message), notificationType, NOOP_LISTENER).notify(null);
    }


    private static String escapeString(String string) {
        return string.replaceAll("\n", "\n<br />");
    }
}
