package com.developerphil.adbidea.terminal

object CommandBuilder {


    fun createCommand(env: Environment,
        commandStr: String): Command {
        val os = env.os
        val command = os.defaultTerminal.command
        return when (os) {
            OperationSystem.WINDOWS -> Command(mutableListOf("cmd", "/c", "start", command, "/K",commandStr))
//            gnome-terminal --title="Press Ctrl+C stop adb screenrecord" -- bash -c "adb shell screenrecord /sdcard/22.mp4;exec bash"

            OperationSystem.LINUX -> Command(mutableListOf(command,"--window","--title=\"Press Ctrl+C stop adb screenrecord\"","-- ","bash","-c","\"$commandStr;exec bash\""))
            OperationSystem.MAC_OS -> Command(mutableListOf(commandStr,command ))
        }
    }
    fun createDirectlyCommand(env: Environment,
        commandStr: String): Command {
        val os = env.os
        val command = os.defaultTerminal.command
        return when (os) {
            OperationSystem.WINDOWS -> Command(mutableListOf("cmd", "/c","/K",commandStr))
            OperationSystem.LINUX -> Command(mutableListOf(command, commandStr))
            OperationSystem.MAC_OS -> Command(mutableListOf(commandStr,command ))
        }
    }
}
