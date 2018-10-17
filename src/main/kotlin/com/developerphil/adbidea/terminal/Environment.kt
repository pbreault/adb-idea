package com.developerphil.adbidea.terminal

class Environment(val os: OperationSystem, val osVersion: String, val gui: String) {

    override fun toString(): String {
        return "Environment{" +
            "os=" + os +
            ", osVersion='" + osVersion + '\''.toString() +
            ", gui='" + gui + '\''.toString() +
            '}'.toString()
    }

    companion object {

        val environment: Environment
            get() {
                val osName = System.getProperty("os.name")
                val osVersion = System.getProperty("os.version")
                val gui = System.getProperty("sun.desktop")

                val os = OperationSystem.fromString(osName)

                return Environment(os, osVersion, gui)
            }
    }
}
