package io.github.raghavsatyadev.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.android.ddmlib.IShellOutputReceiver
import com.android.ddmlib.MultiLineReceiver
import com.android.tools.idea.run.activity.ActivityLocator.ActivityLocatorException
import com.google.common.base.Joiner
import com.google.common.base.Strings
import io.github.raghavsatyadev.adbidea.adb.ShellCommandsFactory.startComponent
import io.github.raghavsatyadev.adbidea.debugger.Debugger
import io.github.raghavsatyadev.adbidea.ui.NotificationHelper.error
import io.github.raghavsatyadev.adbidea.ui.NotificationHelper.info
import java.util.concurrent.TimeUnit

class StartDefaultActivityCommand(private val withDebugger: Boolean) : Command {
  override fun run(context: CommandContext): Boolean =
    with(context) {
      try {
        val component = device.resolveLauncherComponent(packageName, tvDevice = false)
        if (component.isNullOrEmpty()) {
          error("Start fail... " + "Activity not found")
          return false
        }
        val receiver = StartActivityReceiver()
        val shellCommand = startComponent(component, withDebugger)
        device.executeShellCommand(shellCommand, receiver, 15L, TimeUnit.SECONDS)
        if (withDebugger) {
          Debugger(project, device, packageName, coroutineScope).attach()
        }
        if (receiver.isSuccess) {
          info(String.format("<b>%s</b> started on %s", packageName, device.name))
          return true
        } else {
          error(
            String.format(
              "<b>%s</b> could not be started on %s. \n\n<b>ADB Output:</b> \n%s",
              packageName,
              device.name,
              receiver.message,
            )
          )
        }
      } catch (e: Exception) {
        error("Start fail... " + e.message)
      }
      return false
    }

  @Throws(ActivityLocatorException::class)
  private fun getDefaultActivityName(packageName: String, device: IDevice): String? {
    return device.resolveLauncherComponent(packageName, tvDevice = false)
  }

  fun IDevice.resolveLauncherComponent(pkg: String, tvDevice: Boolean): String? {
    val category =
      if (tvDevice) "android.intent.category.LEANBACK_LAUNCHER"
      else "android.intent.category.LAUNCHER"

    val cmd =
      """
        cmd package resolve-activity \
        -a android.intent.action.MAIN \
        -c $category \
        --brief $pkg
    """
        .trimIndent()

    val out = StringBuilder()
    executeShellCommand(
      cmd,
      object : IShellOutputReceiver {
        override fun addOutput(data: ByteArray?, offset: Int, length: Int) {
          data?.let { out.append(String(it)) }
        }

        override fun flush() {}

        override fun isCancelled(): Boolean {
          return false
        }
      },
    )
    // Typical output: "com.example/.MainActivity"
    val line = out.lines().firstOrNull { it.contains('/') } ?: return null
    return line.trim()
  }

  class StartActivityReceiver : MultiLineReceiver() {
    var message = "Nothing Received"
    var currentLines: MutableList<String?> = ArrayList()

    override fun processNewLines(strings: Array<String>) {
      for (s in strings) {
        if (!Strings.isNullOrEmpty(s)) {
          currentLines.add(s)
        }
      }
      computeMessage()
    }

    private fun computeMessage() {
      message = Joiner.on("\n").join(currentLines)
    }

    override fun isCancelled(): Boolean {
      return false
    }

    val isSuccess: Boolean
      get() = currentLines.size in 1..2
  }
}
