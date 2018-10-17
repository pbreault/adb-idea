package com.developerphil.adbidea.terminal

import java.io.IOException
import java.util.*

class Command(val commands: MutableList<String>) {


    fun add(vararg commands: String) {
        this.commands.addAll(Arrays.asList(*commands))
    }

    @Throws(IOException::class)
    fun execute() {
        ProcessBuilder(commands)
            .start()
    }


}
