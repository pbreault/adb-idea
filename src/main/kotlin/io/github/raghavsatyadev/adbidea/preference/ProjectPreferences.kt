package io.github.raghavsatyadev.adbidea.preference

import io.github.raghavsatyadev.adbidea.preference.accessor.PreferenceAccessor

private const val SELECTED_SERIALS_PROPERTY = "io.github.raghavsatyadev.adbidea.selecteddevices"

class ProjectPreferences(private val preferenceAccessor: PreferenceAccessor) {

  fun saveSelectedDeviceSerials(serials: List<String>) {
    preferenceAccessor.saveString(SELECTED_SERIALS_PROPERTY, serials.joinToString(separator = " "))
  }

  fun getSelectedDeviceSerials(): List<String> {
    return with(preferenceAccessor.getString(SELECTED_SERIALS_PROPERTY, "")) {
      if (isEmpty()) {
        emptyList()
      } else {
        split(" ")
      }
    }
  }
}
