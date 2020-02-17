package com.developerphil.adbidea.ui

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import javax.swing.event.HyperlinkEvent

object NotificationHelper {
    private val INFO = NotificationGroup.logOnlyGroup("ADB Idea (Logging)")
    private val ERRORS = NotificationGroup.balloonGroup("ADB Idea (Errors)")
    private val NOOP_LISTENER = NotificationListener { _: Notification?, _: HyperlinkEvent? -> }

    fun info(message: String) = sendNotification(message, NotificationType.INFORMATION, INFO)

    fun error(message: String) = sendNotification(message, NotificationType.ERROR, ERRORS)

    private fun sendNotification(message: String, notificationType: NotificationType, notificationGroup: NotificationGroup) {
        notificationGroup.createNotification("ADB IDEA", escapeString(message), notificationType, NOOP_LISTENER).notify(null)
    }

    private fun escapeString(string: String) = string.replace("\n".toRegex(), "\n<br />")
}