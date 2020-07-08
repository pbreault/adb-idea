package com.developerphil.adbidea.ui

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType

object NotificationHelper {
    private val INFO = NotificationGroup("ADB Idea (Logging)", NotificationDisplayType.NONE, true, null, null)
    private val ERRORS = NotificationGroup("ADB Idea (Errors)", NotificationDisplayType.BALLOON, true, null, null)

    fun info(message: String) = sendNotification(message, NotificationType.INFORMATION, INFO)

    fun error(message: String) = sendNotification(message, NotificationType.ERROR, ERRORS)

    private fun sendNotification(message: String, notificationType: NotificationType, notificationGroup: NotificationGroup) {
        notificationGroup.createNotification("ADB IDEA", escapeString(message), notificationType, null).notify(null)
    }

    private fun escapeString(string: String) = string.replace("\n".toRegex(), "\n<br />")
}