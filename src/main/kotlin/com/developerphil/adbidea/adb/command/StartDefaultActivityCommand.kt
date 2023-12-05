package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.android.ddmlib.MultiLineReceiver
import com.android.tools.idea.run.activity.ActivityLocator.ActivityLocatorException
import com.android.tools.idea.run.activity.DefaultActivityLocator
import com.developerphil.adbidea.adb.ShellCommandsFactory.startActivity
import com.developerphil.adbidea.debugger.Debugger
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.google.common.base.Joiner
import com.google.common.base.Strings
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.ThrowableComputable
import org.jetbrains.android.facet.AndroidFacet
import java.util.*
import java.util.concurrent.TimeUnit

class StartDefaultActivityCommand(private val withDebugger: Boolean) : Command {
    override fun run(context: CommandContext): Boolean = with(context) {
        try {
            val activityName = getDefaultActivityName(facet, device)
            val receiver = StartActivityReceiver()
            val shellCommand = startActivity(packageName, activityName, withDebugger)
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
                        receiver.message
                    )
                )
            }
        } catch (e: Exception) {
            error("Start fail... " + e.message)
        }
        return false
    }

    @Throws(ActivityLocatorException::class)
    private fun getDefaultActivityName(facet: AndroidFacet, device: IDevice): String {
        return ApplicationManager.getApplication()
            .runReadAction(ThrowableComputable<String, ActivityLocatorException?> {
                DefaultActivityLocator(facet).getQualifiedActivityName(
                    device
                )
            })
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