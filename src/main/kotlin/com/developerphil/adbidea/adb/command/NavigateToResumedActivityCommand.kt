package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.android.tools.idea.actions.PsiClassNavigation
import com.developerphil.adbidea.adb.AdbUtil
import com.developerphil.adbidea.adb.command.receiver.GenericReceiver
import com.developerphil.adbidea.invokeLater
import com.developerphil.adbidea.ui.NotificationHelper.error
import com.developerphil.adbidea.ui.NotificationHelper.info
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.util.concurrent.TimeUnit

class NavigateToResumedActivityCommand : Command {

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        try {
            if (AdbUtil.isAppInstalled(device, packageName)) {
                val activityClassPath = device.findResumedActivityClassPath()
                val classNavigations = PsiClassNavigation.getNavigationForClass(
                    project,
                    activityClassPath
                )?.filterNotNull()

                when {
                    activityClassPath == null -> error("Couldn't find resumed activity! Make sure that screen is turned on and device unlocked")
                    classNavigations.isNullOrEmpty() -> error("Couldn't find class in the project. ClassPath=$activityClassPath")
                    else -> navigate(classNavigations)
                }
                return true
            } else {
                error("<b>$packageName</b> is not installed on ${device.name}")
            }
        } catch (e1: Exception) {
            error("Couldn't find resumed activity... " + e1.message)
        }
        return false
    }

    private fun IDevice.findResumedActivityClassPath(): String? {
        val receiver = GenericReceiver()
        executeShellCommand("dumpsys activity | grep mResumedActivity", receiver, 15L, TimeUnit.SECONDS)
        return receiver.adbOutputLines.mapNotNull { it.parseFullActivityClassPath() }.firstOrNull()
    }

    private fun String.parseFullActivityClassPath(): String? {
        val activityMatch = ACTIVITY_RECORD_REGEX.find(this)?.groups?.lastOrNull()?.value ?: return null
        val (packageName, classPath) = activityMatch.split("/")
        val isRelativeClassPath = classPath.startsWith(".")
        return if (isRelativeClassPath) packageName + classPath else classPath
    }

    private fun navigate(navigations: List<PsiClassNavigation>) {
        if (navigations.size > 1) {
            val classes = navigations.joinToString(prefix = "[", postfix = "]") { it.psiFile.virtualFile.path }
            info("Found multiple implementations $classes, navigating to the first one")
        }
        invokeLater {
            navigations.first().navigate(true)
        }
    }

    private companion object {
        val ACTIVITY_RECORD_REGEX = Regex("""ActivityRecord\{.* .* ([\w/\\.]*) .*}""")
    }
}