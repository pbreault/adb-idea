package io.github.raghavsatyadev.adbidea.preference

import com.intellij.util.text.SemVer
import io.github.raghavsatyadev.adbidea.preference.accessor.PreferenceAccessor
import java.util.*

private const val PREVIOUS_VERSION_PROPERTY = "io.github.raghavsatyadev.adbidea.previousversion"

class ApplicationPreferences(private val preferenceAccessor: PreferenceAccessor) {

  fun savePreviousPluginVersion(semVer: SemVer) {
    preferenceAccessor.saveString(PREVIOUS_VERSION_PROPERTY, semVer.toString())
  }

  fun getPreviousPluginVersion(): Optional<SemVer> {
    val version = preferenceAccessor.getString(PREVIOUS_VERSION_PROPERTY, defaultValue = "")
    return SemVer.parseFromText(version)?.let { Optional.of(it) } ?: Optional.empty()
  }
}
