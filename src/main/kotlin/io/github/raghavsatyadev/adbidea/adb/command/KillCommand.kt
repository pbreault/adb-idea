package io.github.raghavsatyadev.adbidea.adb.command

import io.github.raghavsatyadev.adbidea.adb.AdbUtil
import io.github.raghavsatyadev.adbidea.adb.command.receiver.GenericReceiver
import io.github.raghavsatyadev.adbidea.ui.NotificationHelper.error
import io.github.raghavsatyadev.adbidea.ui.NotificationHelper.info
import java.util.concurrent.TimeUnit

class KillCommand : Command {
  override fun run(context: CommandContext): Boolean =
    with(context) {
      try {
        if (AdbUtil.isAppInstalled(device, packageName)) {
          device.executeShellCommand(
            "am force-stop $packageName",
            GenericReceiver(),
            15L,
            TimeUnit.SECONDS,
          )
          val message = String.format("<b>%s</b> forced-stop on %s", packageName, device.name)
          info(message)
          return true
        } else {
          error(String.format("<b>%s</b> is not installed on %s", packageName, device.name))
        }
      } catch (e1: Exception) {
        error("Kill fail... " + e1.message)
      }
      return false
    }
}
