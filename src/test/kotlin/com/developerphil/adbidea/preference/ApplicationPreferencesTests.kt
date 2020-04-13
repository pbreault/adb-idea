package com.developerphil.adbidea.preference

import com.developerphil.adbidea.preference.accessor.InMemoryPreferenceAccessor
import com.google.common.truth.Truth.assertThat
import com.intellij.util.text.SemVer
import org.junit.Test
import java.util.*

class ApplicationPreferencesTests {

    private val prefAccessor = InMemoryPreferenceAccessor()
    private val prefs: ApplicationPreferences = ApplicationPreferences(prefAccessor)

    @Test
    fun `If no previous version is saved, return an empty optional`() {
        assertThat(prefs.getPreviousPluginVersion()).isEqualTo(Optional.empty<SemVer>())
    }

    @Test
    fun `If a previous version is saved, return it`() {
        verifySaveAndRetrieveVersion("0.0.0")
        verifySaveAndRetrieveVersion("1.2.3")
        verifySaveAndRetrieveVersion("1.5.4")
        verifySaveAndRetrieveVersion("1.6.0-SNAPSHOT")
    }

    private fun verifySaveAndRetrieveVersion(version: String) {
        prefAccessor.clear()
        val semVer = SemVer.parseFromText(version)!!
        prefs.savePreviousPluginVersion(semVer)
        assertThat(prefs.getPreviousPluginVersion()).isEqualTo(Optional.of(semVer))
    }

}