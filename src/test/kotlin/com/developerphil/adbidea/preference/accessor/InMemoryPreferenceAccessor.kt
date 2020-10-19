package com.developerphil.adbidea.preference.accessor

class InMemoryPreferenceAccessor : PreferenceAccessor {
    private val prefs = mutableMapOf<String, String>()

    override fun saveString(key: String, value: String) {
        prefs[key] = value
    }

    override fun getString(key: String, defaultValue: String): String {
        return prefs.getOrDefault(key, defaultValue)
    }

    fun clear() = prefs.clear()
}