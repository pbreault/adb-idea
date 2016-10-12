package com.developerphil.adbidea

import com.developerphil.adbidea.accessor.preference.PreferenceAccessor

interface PluginPreferences {
    fun saveSelectedDeviceSerials(serials: List<String>)
    fun getSelectedDeviceSerials(): List<String>
}

class PluginPreferencesImpl(val preferenceAccessor: PreferenceAccessor) : PluginPreferences {

    private val SELECTED_SERIALS_PROPERTY = "com.developerphil.adbidea.selecteddevices"


    override fun saveSelectedDeviceSerials(serials: List<String>) {
        preferenceAccessor.saveString(SELECTED_SERIALS_PROPERTY, serials.joinToString(separator = " "))
    }

    override fun getSelectedDeviceSerials(): List<String> {
        return with(preferenceAccessor.getString(SELECTED_SERIALS_PROPERTY, "")) {
            if (isEmpty()) {
                emptyList()
            } else {
                split(" ")
            }
        }
    }
}