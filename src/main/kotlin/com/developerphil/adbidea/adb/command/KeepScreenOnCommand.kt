package com.developerphil.adbidea.adb.command

import com.developerphil.adbidea.adb.AdbUtil
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.ui.NotificationHelper
import java.util.concurrent.TimeUnit

class KeepScreenOnCommand(private val commandMode: CommandMode) : Command {
    companion object {
        private var screenTimeOut = 30000L
    }

    enum class CommandMode {
        TURN_ON, TURN_OFF, SAVE_TIMEOUT
    }

    override fun run(context: CommandContext): Boolean = with(context) {
        try {
            if (AdbUtil.isAppInstalled(device, packageName)) {
                val putCommand = "settings put system screen_off_timeout "
                if (commandMode == CommandMode.TURN_ON) {
                    keepScreenOn(putCommand)
                } else if (commandMode == CommandMode.TURN_OFF) {
                    turnOffKeepScreenOn(putCommand)
                } else {
                    saveScreenTimeout()
                }


                return true
            } else {
                NotificationHelper.error(String.format("Some error occurred", device.name))
            }
        } catch (e1: Exception) {
            NotificationHelper.error("Error occurred " + e1.message)
        }
        return false
    }

    private fun CommandContext.turnOffKeepScreenOn(putCommand: String) {
        device.executeShellCommand(
            putCommand + screenTimeOut,
            GenericReceiver(),
            15L,
            TimeUnit.SECONDS
        )
        val successMessage = "Keep Screen On feature is turned off."
        NotificationHelper.success(
            String.format(
                successMessage,
                device.name
            )
        )
    }

    private fun CommandContext.keepScreenOn(putCommand: String) {
        saveScreenTimeout()
        device.executeShellCommand(
            putCommand + "600000",
            GenericReceiver(),
            15L,
            TimeUnit.SECONDS
        )
        val successMessage =
            "%s's screen will stay on for 10 minutes without interaction. Please fire command turn off 'keep screen on' after usage."
        NotificationHelper.success(
            String.format(
                successMessage,
                device.name
            )
        )
    }

    private fun CommandContext.saveScreenTimeout() {
        val receiver = GenericReceiver()
        val getCommand = "settings get system screen_off_timeout"
        device.executeShellCommand(
            getCommand,
            receiver,
            15L,
            TimeUnit.SECONDS
        )
        val output = receiver.adbOutputLines
        if (output.isNotEmpty()) {
            val timeout = output[0].trim { it <= ' ' }
            screenTimeOut = timeout.toLong()
        }
    }
}