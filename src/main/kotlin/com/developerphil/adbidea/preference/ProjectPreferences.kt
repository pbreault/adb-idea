package com.developerphil.adbidea.preference

import com.developerphil.adbidea.preference.accessor.PreferenceAccessor

private const val SELECTED_SERIALS_PROPERTY = "com.developerphil.adbidea.selecteddevices"

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