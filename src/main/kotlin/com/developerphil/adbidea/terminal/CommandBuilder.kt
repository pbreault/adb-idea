package com.developerphil.adbidea.terminal

object CommandBuilder {


    fun createCommand(env: Environment,
        commandStr: String): Command {
        val os = env.os
        val command = os.defaultTerminal.command
        when (os) {
            OperationSystem.WINDOWS -> return Command(mutableListOf("cmd", "/c", "start", command, "/K",commandStr))
            OperationSystem.LINUX -> return Command(mutableListOf(command, commandStr))
            OperationSystem.MAC_OS -> return Command(mutableListOf(commandStr,command ))
            else -> throw RuntimeException("The environment is not supported: $os")
        }
    }
}
