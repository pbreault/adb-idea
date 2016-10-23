package com.developerphil.adbidea.adb

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.PluginPreferences
import javax.inject.Inject

class UseSameDevicesHelper @Inject constructor(private val pluginPreferences: PluginPreferences, private val bridge: Bridge) {

    var previouslyConnectedDevices: List<IDevice>? = null

    fun getRememberedDevices(): List<IDevice> {
        val selectedDeviceSerials = pluginPreferences.getSelectedDeviceSerials()
        val currentlyConnectedDevices = bridge.connectedDevices()

        if (currentlyConnectedDevices == previouslyConnectedDevices) {
            val rememberedDevices = currentlyConnectedDevices.filter { selectedDeviceSerials.contains(it.serialNumber) }
            if (rememberedDevices.size == selectedDeviceSerials.size) {
                return rememberedDevices
            }
        }

        return emptyList()
    }

    fun rememberDevices() {
        previouslyConnectedDevices = bridge.connectedDevices()
    }
}