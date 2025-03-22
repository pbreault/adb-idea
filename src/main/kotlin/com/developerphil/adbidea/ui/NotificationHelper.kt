package com.developerphil.adbidea.ui

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType

object NotificationHelper {
    fun info(message: String) {
        sendNotification(
            message,
            NotificationType.INFORMATION,
            NotificationGroupManager
                .getInstance()
                .getNotificationGroup("ADB Idea (Logging)")
        )
    }

    // Function to send an error notification
    fun error(message: String) {
        sendNotification(
            message,
            NotificationType.ERROR,
            NotificationGroupManager
                .getInstance()
                .getNotificationGroup("ADB Idea (Errors)")
        )
    }

    // Helper function to create and display a notification
    private fun sendNotification(
        message: String,
        notificationType: NotificationType,
        notificationGroup: NotificationGroup,
    ) {
        // Create the notification without a listener
        val notification = notificationGroup.createNotification(
            "ADB IDEA",
            escapeString(message),
            notificationType,
        )

        // Display the notification
        notification.notify(null)
    }

    private fun escapeString(string: String) = string.replace(
        "\n".toRegex(),
        "\n<br />"
    )
}