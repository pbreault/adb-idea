package io.github.raghavsatyadev.adbidea.adb.command

import io.github.raghavsatyadev.adbidea.adb.command.receiver.GenericReceiver
import io.github.raghavsatyadev.adbidea.ui.NotificationHelper.error
import io.github.raghavsatyadev.adbidea.ui.NotificationHelper.info
import java.util.concurrent.TimeUnit

enum class SvcCommand(val parameter: String, val description: String) {
  WIFI("wifi", "Wi-Fi"),
  MOBILE("data", "Mobile data"),
}

class ToggleSvcCommand(private val command: SvcCommand, private val enable: Boolean) : Command {

  private val shellCommand = "svc ${command.parameter} ${enable.toState()}"

  override fun run(context: CommandContext): Boolean =
    with(context) {
      try {
        device.executeShellCommand(shellCommand, GenericReceiver(), 30L, TimeUnit.SECONDS)
        info(
          String.format(
            "<b>%s</b> %s%s on %s",
            command.description,
            enable.toState(),
            "d",
            device.name,
          )
        )
        return true
      } catch (e: Exception) {
        error(
          "Failure while attempting to ${enable.toState()} ${command.description} on ${device.name}: " +
            e.message
        )
      }
      return false
    }

  private fun Boolean.toState() = if (this) "enable" else "disable"
}
