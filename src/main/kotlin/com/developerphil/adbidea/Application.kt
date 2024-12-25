package com.developerphil.adbidea

import com.developerphil.adbidea.preference.ApplicationPreferences
import com.developerphil.adbidea.preference.accessor.PreferenceAccessorImpl
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.text.SemVer

// This is more of a service locator than a proper DI framework.
// It's not used often enough in the codebase to warrant the complexity of a DI solution like dagger.
@Service
class Application {

    private val logger = Logger.getInstance(Application::class.java)
    private val pluginPackage = "com.developerphil.adbidea"
    private val applicationPreferencesAccessor = PreferenceAccessorImpl(PropertiesComponent.getInstance())
    private val applicationPreferences = ApplicationPreferences(applicationPreferencesAccessor)

    init {
        try {
            val pluginId = PluginId.getId(pluginPackage)
            val pluginDescriptor = PluginManagerCore.getPlugin(pluginId)
            val version = pluginDescriptor?.version
            if (version != null) {
                applicationPreferences.savePreviousPluginVersion(SemVer.parseFromText(version)!!)
            } else {
                logger.error("Plugin version is null for plugin ID: $pluginId")
            }
        } catch (e: Exception) {
            NotificationHelper.error("Couldn't initialize ADB Idea: ${e.message}")
        }
    }
}