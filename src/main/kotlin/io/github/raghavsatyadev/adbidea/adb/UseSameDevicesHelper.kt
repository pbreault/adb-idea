package io.github.raghavsatyadev.adbidea.adb

import com.android.ddmlib.IDevice
import io.github.raghavsatyadev.adbidea.preference.ProjectPreferences

class UseSameDevicesHelper(
    private val projectPreferences: ProjectPreferences,
    private val bridge: Bridge,
) {

    var previouslyConnectedDevices: List<IDevice>? = null

    fun getRememberedDevices(): List<IDevice> {
        val selectedDeviceSerials = projectPreferences.getSelectedDeviceSerials()
        val currentlyConnectedDevices = bridge.connectedDevices()

        if (currentlyConnectedDevices == previouslyConnectedDevices) {
            val rememberedDevices =
                currentlyConnectedDevices.filter { selectedDeviceSerials.contains(it.serialNumber) }
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
