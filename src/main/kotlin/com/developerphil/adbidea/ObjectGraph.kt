package com.developerphil.adbidea

import com.developerphil.adbidea.preference.accessor.PreferenceAccessorImpl
import com.developerphil.adbidea.adb.BridgeImpl
import com.developerphil.adbidea.adb.DeviceResultFetcher
import com.developerphil.adbidea.adb.UseSameDevicesHelper
import com.developerphil.adbidea.preference.ProjectPreferences
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

// This is more of a service locator than a proper DI framework.
// It's not used often enough in the codebase to warrant the complexity of a DI solution like dagger.
@Service(Service.Level.PROJECT)
class ObjectGraph(private val project: Project, private val coroutineScope: CoroutineScope) {

    val deviceResultFetcher by lazy { DeviceResultFetcher(project, useSameDevicesHelper, bridge) }
    val projectPreferences: ProjectPreferences by lazy { ProjectPreferences(projectPreferenceAccessor) }
    val projectScope: CoroutineScope = coroutineScope

    private val useSameDevicesHelper by lazy { UseSameDevicesHelper(projectPreferences, bridge) }
    private val projectPreferenceAccessor by lazy { PreferenceAccessorImpl(PropertiesComponent.getInstance(project)) }
    private val bridge by lazy { BridgeImpl(project) }
}
