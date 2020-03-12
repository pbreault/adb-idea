package com.developerphil.adbidea

import com.developerphil.adbidea.accessor.preference.ProjectPreferenceAccessor
import com.developerphil.adbidea.adb.BridgeImpl
import com.developerphil.adbidea.adb.DeviceResultFetcher
import com.developerphil.adbidea.adb.UseSameDevicesHelper
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project

// This is more of a service locator than a proper DI framework.
// It's not used often enough in the codebase to warrant the complexity of a DI solution like dagger.
class ObjectGraph(private val project: Project) : ProjectComponent {

    val deviceResultFetcher by lazy { DeviceResultFetcher(project, useSameDevicesHelper, bridge) }
    val pluginPreferences: PluginPreferences by lazy { PluginPreferencesImpl(preferenceAccessor) }

    private val useSameDevicesHelper by lazy { UseSameDevicesHelper(pluginPreferences, bridge) }
    private val preferenceAccessor by lazy { ProjectPreferenceAccessor(project) }
    private val bridge by lazy { BridgeImpl(project) }


    // Project Component Boilerplate
    override fun projectOpened() = Unit

    override fun projectClosed() = Unit
    override fun initComponent() = Unit
    override fun disposeComponent() = Unit
    override fun getComponentName(): String = "InjectionObjectGraph"
}
