package com.developerphil.adbidea.preference.accessor

import com.intellij.ide.util.PropertiesComponent

class PreferenceAccessorImpl(private val propertiesComponent: PropertiesComponent) : PreferenceAccessor {

    override fun saveString(key: String, value: String) {
        propertiesComponent.setValue(key, value)
    }

    override fun getString(key: String, defaultValue: String): String {
        return propertiesComponent.getValue(key) ?: defaultValue
    }
}