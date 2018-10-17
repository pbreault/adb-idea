package com.developerphil.adbidea.terminal

import com.developerphil.adbidea.terminal.Terminal.*

enum class OperationSystem  constructor( val nameStr: String, val defaultTerminal: Terminal){
    WINDOWS("win", COMMAND_PROMPT),
    LINUX("lin", GNOME_TERMINAL),
    MAC_OS("mac", MAC_TERMINAL);


    companion object {

        fun fromString(osName: String): OperationSystem {
            val os = osName.substring(0, 3).toLowerCase()
            return if (WINDOWS.nameStr == os) {
                WINDOWS
            } else if (LINUX.nameStr == os) {
                LINUX
            } else if (MAC_OS.nameStr == os) {
                MAC_OS
            } else {
                throw RuntimeException("This Operation System is not supported: $osName ($os)")
            }
        }
    }
}
