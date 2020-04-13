package com.developerphil.adbidea.preference

import com.developerphil.adbidea.preference.accessor.InMemoryPreferenceAccessor
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProjectPreferencesTests {

    private val prefAccessor = InMemoryPreferenceAccessor()
    private val prefs: ProjectPreferences = ProjectPreferences(prefAccessor)

    @Test
    fun `Can save a single selected device`() {
        val serials = listOf("first")
        prefs.saveSelectedDeviceSerials(serials)
        assertThat(prefs.getSelectedDeviceSerials()).isEqualTo(serials)
    }

    @Test
    fun `Can save multiple selected devices`() {
        val serials = listOf("first", "second", "third")
        prefs.saveSelectedDeviceSerials(serials)
        assertThat(prefs.getSelectedDeviceSerials()).isEqualTo(serials)
    }

    @Test
    fun `Can save an empty selected devices list`() {
        val serials = emptyList<String>()
        prefs.saveSelectedDeviceSerials(serials)
        assertThat(prefs.getSelectedDeviceSerials()).isEqualTo(serials)
    }

    @Test
    fun `When no selected serials are saved, return an empty list`() {
        assertThat(prefs.getSelectedDeviceSerials()).isEqualTo(emptyList<String>())
    }

}