package com.developerphil.adbidea.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * Created by pbreault on 1/2/14.
 */
public class NotificationHelper {
    public static void info(String message) {
        sendNotification(message, NotificationType.INFORMATION);
    }

    public static void error(String message) {
        sendNotification(message, NotificationType.ERROR);
    }

    public static void sendNotification(String message, NotificationType notificationType) {
        Notification notification = new Notification("com.developerphil.adbidea", "ADB IDEA", espaceString(message), notificationType);
        Notifications.Bus.notify(notification);
    }


    private static String espaceString(String string) {
        // replace with both so that it returns are preserved in the notification ballon and in the event log
        return string.replaceAll("\n", "\n<br />");
    }
}
