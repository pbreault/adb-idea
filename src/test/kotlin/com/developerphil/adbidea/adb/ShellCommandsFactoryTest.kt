package com.developerphil.adbidea.adb

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ShellCommandsFactoryTest {

    @Test
    fun startActivityWithoutDebugger() {

        val command = ShellCommandsFactory.startActivity(
                packageName = "com.example",
                activityName = "com.example.MyActivity",
                attachDebugger = false)

        assertThat(command).isEqualTo("am start -n com.example/com.example.MyActivity")
    }

    @Test
    fun startActivityWithDebugger() {
        val command = ShellCommandsFactory.startActivity(
                packageName = "com.example",
                activityName = "com.example.MyActivity",
                attachDebugger = true)

        assertThat(command).isEqualTo("am start -D -n com.example/com.example.MyActivity")
    }
}