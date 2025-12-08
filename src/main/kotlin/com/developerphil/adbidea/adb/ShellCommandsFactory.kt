package com.developerphil.adbidea.adb

object ShellCommandsFactory {

    @JvmStatic
    fun startActivity(packageName: String, activityName: String, attachDebugger: Boolean): String {
        val debugFlag = if (attachDebugger) "-D " else ""
        return "am start -a android.intent.action.MAIN $debugFlag-n $packageName/$activityName"
    }
}
