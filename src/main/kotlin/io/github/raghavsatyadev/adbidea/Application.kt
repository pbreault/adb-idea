package io.github.raghavsatyadev.adbidea

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.text.SemVer
import io.github.raghavsatyadev.adbidea.preference.ApplicationPreferences
import io.github.raghavsatyadev.adbidea.preference.accessor.PreferenceAccessorImpl
import io.github.raghavsatyadev.adbidea.ui.NotificationHelper

// This is more of a service locator than a proper DI framework.
// It's not used often enough in the codebase to warrant the complexity of a DI solution like
// dagger.
@Service
class Application {

  private val logger = Logger.getInstance(Application::class.java)
  private val pluginPackage = "io.github.raghavsatyadev.adbidea"
  private val applicationPreferencesAccessor =
    PreferenceAccessorImpl(PropertiesComponent.getInstance())
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
      NotificationHelper.error("Couldn't initialize ADB Menu: ${e.message}")
    }
  }
}
