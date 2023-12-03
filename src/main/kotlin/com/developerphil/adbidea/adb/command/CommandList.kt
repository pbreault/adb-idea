package com.developerphil.adbidea.adb.command

open class CommandList(vararg commands: Command) : Command {

    private val commands = listOf(*commands)

    override fun run(context: CommandContext): Boolean {
        for (command in commands) {
            if (!command.run(context)) {
                return false
            }
        }
        return true
    }

}