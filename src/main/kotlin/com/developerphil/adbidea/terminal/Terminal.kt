package com.developerphil.adbidea.terminal

enum class Terminal  constructor(val command: String) {
    COMMAND_PROMPT("cmd"),
    POWER_SHELL("powershell"),
    CON_EMU("conemu"),
    GIT_BASH("git-bash"),
    GNOME_TERMINAL("gnome-terminal"),
    RXVT("rxvt"),
    MAC_TERMINAL("Terminal"),
    I_TERM("iTerm"),
    GENERIC("");


    companion object {

        fun fromString(command: String): Terminal {
            return if (containsIgnoreCase(command, COMMAND_PROMPT.command)) {
                COMMAND_PROMPT
            } else if (containsIgnoreCase(command, POWER_SHELL.command)) {
                POWER_SHELL
            } else if (containsIgnoreCase(command, CON_EMU.command)) {
                CON_EMU
            } else if (containsIgnoreCase(command, GIT_BASH.command)) {
                GIT_BASH
            } else if (containsIgnoreCase(command, GNOME_TERMINAL.command)) {
                GNOME_TERMINAL
            } else if (containsIgnoreCase(command, RXVT.command)) {
                RXVT
            } else if (containsIgnoreCase(command, MAC_TERMINAL.command)) {
                MAC_TERMINAL
            } else if (containsIgnoreCase(command, I_TERM.command)) {
                I_TERM
            } else {
                GENERIC
            }
        }

        private fun containsIgnoreCase(s1: String, s2: String): Boolean {
            return s1.toLowerCase().contains(s2.toLowerCase())
        }
    }
}
