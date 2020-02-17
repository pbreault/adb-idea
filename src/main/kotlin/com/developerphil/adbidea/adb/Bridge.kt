package com.developerphil.adbidea.adb

import com.android.ddmlib.IDevice
import com.intellij.openapi.project.Project
import org.jetbrains.android.sdk.AndroidSdkUtils

interface Bridge {
    fun isReady(): Boolean
    fun connectedDevices(): List<IDevice>
}


class BridgeImpl(project: Project) : Bridge {

    val androidBridge = AndroidSdkUtils.getDebugBridge(project)

    override fun isReady(): Boolean {
        if (androidBridge == null) {
            return false
        }

        return androidBridge.isConnected && androidBridge.hasInitialDeviceList()
    }

    override fun connectedDevices(): List<IDevice> {
        return androidBridge?.devices?.asList() ?: emptyList()
    }
}
