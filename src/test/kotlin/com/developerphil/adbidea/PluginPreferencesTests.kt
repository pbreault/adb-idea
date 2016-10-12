package com.developerphil.adbidea

import com.developerphil.adbidea.accessor.preference.PreferenceAccessor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class PluginPreferencesTests {

    val prefAccessor: PreferenceAccessor = mock(PreferenceAccessor::class.java)
    val pluginPrefs: PluginPreferences = PluginPreferencesImpl(prefAccessor)

    @Test
    fun saveSelectedDeviceSerials_canSaveASingleSerial() {
        pluginPrefs.saveSelectedDeviceSerials(listOf("first"))

        verifySave("first")
    }

    @Test
    fun saveSelectedDeviceSerials_canSaveAMultipleSerials() {
        pluginPrefs.saveSelectedDeviceSerials(listOf("first", "second", "third"))

        verifySave("first second third")
    }

    @Test
    fun saveSelectedDeviceSerials_canSaveAnEmptyList() {
        pluginPrefs.saveSelectedDeviceSerials(emptyList())

        verifySave("")
    }

    @Test
    fun getSelectedDeviceSerials_canReturnASingleSerial() {
        givenPreference("first")

        assertEquals(listOf("first"), pluginPrefs.getSelectedDeviceSerials())
    }

    @Test
    fun getSelectedDeviceSerials_canReturnAMultipleSerials() {
        givenPreference("first second third")

        assertEquals(listOf("first", "second", "third"), pluginPrefs.getSelectedDeviceSerials())
    }

    @Test
    fun getSelectedDeviceSerials_canReturnAnEmptyList() {
        givenPreference("")

        assertEquals(emptyList<String>(), pluginPrefs.getSelectedDeviceSerials())
    }

    fun verifySave(string: String) {
        verify(prefAccessor).saveString("com.developerphil.adbidea.selecteddevices", string)
    }

    fun givenPreference(prefs: String) {
        Mockito.`when`(prefAccessor.getString("com.developerphil.adbidea.selecteddevices", "")).thenReturn(prefs)
    }


}