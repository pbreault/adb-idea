package com.developerphil.adbidea.adb.command.receiver

import com.android.ddmlib.MultiLineReceiver
import java.util.*
import java.util.regex.Pattern

data class ResponseHolder(var response: String = "")

class ResponseReceiver(val responseHolder: ResponseHolder)  : MultiLineReceiver() {

    val adbOutputLines: MutableList<String> = ArrayList()

    override fun processNewLines(lines: Array<String>) {
        adbOutputLines.addAll(listOf(*lines))
        for (line in lines) {
            if (line.isNotEmpty()) {
                responseHolder.response += "$line\n"
            }
        }
    }

    override fun isCancelled() = false

}