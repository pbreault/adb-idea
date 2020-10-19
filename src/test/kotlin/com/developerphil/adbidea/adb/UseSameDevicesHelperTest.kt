package com.developerphil.adbidea

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.preference.accessor.InMemoryPreferenceAccessor
import com.developerphil.adbidea.adb.Bridge
import com.developerphil.adbidea.adb.FakeDevice
import com.developerphil.adbidea.adb.UseSameDevicesHelper
import com.developerphil.adbidea.preference.ProjectPreferences
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UseSameDevicesHelperTest {

    val pluginPrefs = ProjectPreferences(InMemoryPreferenceAccessor())
    val bridge = FakeBridge()
    val helper: UseSameDevicesHelper = UseSameDevicesHelper(pluginPrefs, bridge)

    @Test
    fun onFirstRun_shouldNotTryToReturnDevices() {
        assertThat(helper.getRememberedDevices()).isEmpty()
    }

    @Test
    fun whenAskingToRememberDevices_shouldReturnDevicesInSharedPrefs() {
        bridge.willReturn("1", "2", "3", "4")
        pluginPrefs.willReturn("1", "2")

        helper.rememberDevices()
        assertRememberedDevices("1", "2")
    }

    @Test
    fun whenTheConnectedDevicesChanges_shouldNotUseSameDevice() {
        bridge.willReturn("1", "2", "3")
        pluginPrefs.willReturn("1", "2")

        helper.rememberDevices()
        assertRememberedDevices("1", "2")

        bridge.willReturn("1", "2")
        assertNoRememberedDevices()
    }

    @Test
    fun whenSharedPrefsAreEmpty_shouldNotUseSameDevice() {
        bridge.willReturn("1", "2", "3")

        helper.rememberDevices()
        assertNoRememberedDevices()
    }

    @Test
    fun whenSharedPrefsAndHelperAreNotSynced_shouldNotUseSameDevice() {
        bridge.willReturn("1", "2", "3", "4")
        pluginPrefs.willReturn("2", "4")

        helper.rememberDevices()

        assertRememberedDevices("2", "4")

        pluginPrefs.willReturn("2", "id_of_device_not_connected_right_now")
        assertNoRememberedDevices()
    }

    fun assertNoRememberedDevices() {
        assertThat(helper.getRememberedDevices()).isEmpty()
    }

    fun assertRememberedDevices(vararg ids: String) {
        assertThat(helper.getRememberedDevices()).isEqualTo(ids.map(::FakeDevice).toList())
    }

    class FakeBridge : Bridge {
        var ready = true
        var devices: List<IDevice> = emptyList()

        override fun isReady(): Boolean {
            return ready
        }

        override fun connectedDevices(): List<IDevice> {
            return devices
        }

        fun willReturn(vararg ids: String) {
            devices = ids.map(::FakeDevice)
        }
    }

    private fun ProjectPreferences.willReturn(vararg ids: String) = saveSelectedDeviceSerials(ids.asList())
}