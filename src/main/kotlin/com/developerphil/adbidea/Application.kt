package com.developerphil.adbidea

import com.developerphil.adbidea.preference.accessor.PreferenceAccessorImpl
import com.developerphil.adbidea.preference.ApplicationPreferences
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.text.SemVer


private val pluginPackage = "com.developerphil.adbidea"

// This is more of a service locator than a proper DI framework.
// It's not used often enough in the codebase to warrant the complexity of a DI solution like dagger.
class Application : ApplicationComponent {
    private val applicationPreferencesAccessor = PreferenceAccessorImpl(PropertiesComponent.getInstance())
    private val applicationPreferences = ApplicationPreferences(applicationPreferencesAccessor)

    override fun initComponent() {
        try {
            val version = PluginManager.getPlugin(PluginId.getId(pluginPackage))!!.version!!
            applicationPreferences.savePreviousPluginVersion(SemVer.parseFromText(version)!!)
        } catch (e: Exception) {
            NotificationHelper.error("Couldn't initialize ADB Idea: ${e.message}")
        }
    }
}
