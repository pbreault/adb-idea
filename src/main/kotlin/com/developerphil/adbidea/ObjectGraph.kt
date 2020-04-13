package com.developerphil.adbidea

import com.developerphil.adbidea.preference.accessor.PreferenceAccessorImpl
import com.developerphil.adbidea.adb.BridgeImpl
import com.developerphil.adbidea.adb.DeviceResultFetcher
import com.developerphil.adbidea.adb.UseSameDevicesHelper
import com.developerphil.adbidea.preference.ProjectPreferences
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project

// This is more of a service locator than a proper DI framework.
// It's not used often enough in the codebase to warrant the complexity of a DI solution like dagger.
class ObjectGraph(private val project: Project) : ProjectComponent {

    val deviceResultFetcher by lazy { DeviceResultFetcher(project, useSameDevicesHelper, bridge) }
    val projectPreferences: ProjectPreferences by lazy { ProjectPreferences(projectPreferenceAccessor) }

    private val useSameDevicesHelper by lazy { UseSameDevicesHelper(projectPreferences, bridge) }
    private val projectPreferenceAccessor by lazy { PreferenceAccessorImpl(PropertiesComponent.getInstance(project)) }
    private val bridge by lazy { BridgeImpl(project) }


    // Project Component Boilerplate
    override fun projectOpened() = Unit

    override fun projectClosed() = Unit
    override fun initComponent() = Unit
    override fun disposeComponent() = Unit
    override fun getComponentName(): String = "InjectionObjectGraph"
}
