package io.github.raghavsatyadev.adbidea

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import io.github.raghavsatyadev.adbidea.adb.BridgeImpl
import io.github.raghavsatyadev.adbidea.adb.DeviceResultFetcher
import io.github.raghavsatyadev.adbidea.adb.UseSameDevicesHelper
import io.github.raghavsatyadev.adbidea.preference.ProjectPreferences
import io.github.raghavsatyadev.adbidea.preference.accessor.PreferenceAccessorImpl
import kotlinx.coroutines.CoroutineScope

// This is more of a service locator than a proper DI framework.
// It's not used often enough in the codebase to warrant the complexity of a DI solution like
// dagger.
@Service(Service.Level.PROJECT)
class ObjectGraph(private val project: Project, coroutineScope: CoroutineScope) {

    val deviceResultFetcher by lazy { DeviceResultFetcher(project, useSameDevicesHelper, bridge) }
    val projectPreferences: ProjectPreferences by lazy {
        ProjectPreferences(projectPreferenceAccessor)
    }
    val projectScope: CoroutineScope = coroutineScope

    private val useSameDevicesHelper by lazy { UseSameDevicesHelper(projectPreferences, bridge) }
    private val projectPreferenceAccessor by lazy {
        PreferenceAccessorImpl(PropertiesComponent.getInstance(project))
    }
    private val bridge by lazy { BridgeImpl(project) }
}
