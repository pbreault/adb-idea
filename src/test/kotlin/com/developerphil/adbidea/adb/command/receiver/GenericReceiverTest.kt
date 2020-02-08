package com.developerphil.adbidea.adb.command.receiver

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GenericReceiverTest {
    @Test
    fun testReceiverRecordsAdbOutput() {
        val receiver = GenericReceiver()
        assertThat(receiver.adbOutputLines).isEmpty()

        receiver.processNewLines(arrayOf("1", "2", "3"))
        assertThat(receiver.adbOutputLines).containsExactly("1", "2", "3")

        receiver.processNewLines(arrayOf("4"))
        assertThat(receiver.adbOutputLines).containsExactly("1", "2", "3", "4")
    }
}