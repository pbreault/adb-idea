package com.developerphil.adbidea.preference

import com.developerphil.adbidea.preference.accessor.PreferenceAccessor
import com.intellij.util.text.SemVer
import java.util.*

private const val PREVIOUS_VERSION_PROPERTY = "com.developerphil.adbidea.previousversion"

class ApplicationPreferences(private val preferenceAccessor: PreferenceAccessor) {

    fun savePreviousPluginVersion(semVer: SemVer) {
        preferenceAccessor.saveString(PREVIOUS_VERSION_PROPERTY, semVer.toString())
    }

    fun getPreviousPluginVersion(): Optional<SemVer> {
        val version = preferenceAccessor.getString(PREVIOUS_VERSION_PROPERTY, defaultValue = "")
        return SemVer.parseFromText(version)?.let { Optional.of(it) } ?: Optional.empty()
    }

}