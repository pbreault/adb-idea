package com.developerphil.adbidea.accessor.preference

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

class ProjectPreferenceAccessor(project: Project) : PreferenceAccessor {

    private val properties = PropertiesComponent.getInstance(project)

    override fun saveString(key: String, value: String) {
        properties.setValue(key, value)
    }

    override fun getString(key: String, defaultValue: String) = properties.getValue(key) ?: defaultValue
}