package com.developerphil.adbidea.accessor.preference

interface PreferenceAccessor {
    fun saveString(key: String, value: String)
    fun getString(key: String, defaultValue: String): String
}

