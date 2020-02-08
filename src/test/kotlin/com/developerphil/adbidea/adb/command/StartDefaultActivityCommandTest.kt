package com.developerphil.adbidea.adb.command

import com.developerphil.adbidea.adb.command.StartDefaultActivityCommand.StartActivityReceiver
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StartDefaultActivityCommandTest {

    @Test
    fun testReceiverSuccess() {
        with(StartActivityReceiver()) {
            assertThat(isSuccess).isFalse()
            processNewLines(arrayOf(
                    "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.example.untitled/.MyActivity }"
            ))
            processNewLines(TRAILING_EMPTY_LINE)
            assertThat(isSuccess).isTrue()
        }
    }

    @Test
    fun testIsSuccessWhenAppIsAlreadyStarted() {
        with(StartActivityReceiver()) {
            processNewLines(arrayOf(
                    "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.example.untitled/.MyActivity }",
                    "Warning: Activity not started, its current task has been brought to the front"
            ))
            processNewLines(TRAILING_EMPTY_LINE)
            assertThat(isSuccess).isTrue()
        }
    }

    @Test
    fun testIsFailureWhenAppIsUninstalled() {
        with(StartActivityReceiver()) {
            processNewLines(arrayOf(
                    "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.cxategory.LAUNCHER] cmp=com.example.untitled/.MyActivity }",
                    "Error type 3",
                    "Error: Activity class {com.example.untitled/com.example.untitled.MyActivity} does not exist."
            ))
            processNewLines(TRAILING_EMPTY_LINE)
            assertThat(isSuccess).isFalse()
            assertThat(message).isEqualTo(
                    "Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.cxategory.LAUNCHER] cmp=com.example.untitled/.MyActivity }\n" +
                            "Error type 3\n" +
                            "Error: Activity class {com.example.untitled/com.example.untitled.MyActivity} does not exist."
            )
        }
    }
}

private val TRAILING_EMPTY_LINE = arrayOf("")
