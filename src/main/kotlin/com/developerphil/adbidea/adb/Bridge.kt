package com.developerphil.adbidea.adb

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.intellij.openapi.project.Project
import org.jetbrains.android.sdk.AndroidSdkUtils

interface Bridge {
    fun isReady(): Boolean
    fun connectedDevices(): List<IDevice>
}


class BridgeImpl(private val project: Project) : Bridge {

    private val androidBridge: AndroidDebugBridge?
        get() = AndroidSdkUtils.getDebugBridge(project)

    override fun isReady() = androidBridge?.let {
        it.isConnected && it.hasInitialDeviceList()
    } ?: false

    override fun connectedDevices(): List<IDevice> {
        return androidBridge?.devices?.asList() ?: emptyList()
    }
}
