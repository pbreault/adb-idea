package com.developerphil.adbidea.adb.command.receiver

import com.android.ddmlib.MultiLineReceiver
import java.util.*
import java.util.regex.Pattern

class GenericReceiver : MultiLineReceiver() {

    val adbOutputLines: MutableList<String> = ArrayList()
    private var errorMessage: String? = null

    override fun processNewLines(lines: Array<String>) {
        adbOutputLines.addAll(listOf(*lines))
        for (line in lines) {
            if (line.isNotEmpty()) {
                errorMessage = if (line.startsWith(SUCCESS_OUTPUT)) {
                    null
                } else {
                    val m = FAILURE_PATTERN.matcher(line)
                    if (m.matches()) {
                        m.group(1)
                    } else {
                        "Unknown failure"
                    }
                }
            }
        }
    }

    override fun isCancelled() = false

}

private const val SUCCESS_OUTPUT = "Success" //$NON-NLS-1$
private val FAILURE_PATTERN = Pattern.compile("Failure\\s+\\[(.*)\\]") //$NON-NLS-1$
