package io.github.raghavsatyadev.adbidea.adb

import com.android.ddmlib.IDevice
import com.android.tools.idea.model.AndroidModel
import com.android.tools.idea.projectsystem.gradle.getHolderModule
import com.android.tools.idea.util.androidFacet
import com.intellij.facet.ProjectFacetManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.impl.ModuleImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import io.github.raghavsatyadev.adbidea.adb.DeviceResult.DeviceNotFound
import io.github.raghavsatyadev.adbidea.adb.DeviceResult.SuccessfulDeviceResult
import io.github.raghavsatyadev.adbidea.compatibility.BackwardCompatibleGetter
import io.github.raghavsatyadev.adbidea.ui.DeviceChooserDialog
import io.github.raghavsatyadev.adbidea.ui.ModuleChooserDialogHelper
import io.github.raghavsatyadev.adbidea.ui.NotificationHelper
import org.jetbrains.android.facet.AndroidFacet
import org.joor.Reflect

class DeviceResultFetcher(
  private val project: Project,
  private val useSameDevicesHelper: UseSameDevicesHelper,
  private val bridge: Bridge,
) {

  fun fetch(): DeviceResult? {
    val facets = ProjectFacetManager.getInstance(project).getFacets(AndroidFacet.ID)
    if (facets.isNotEmpty()) {
      val facet = getFacet(facets) ?: return null

      val packageName = AndroidModel.get(facet)?.applicationId ?: return null

      if (!bridge.isReady()) {
        NotificationHelper.error("No platform configured")
        return null
      }

      val rememberedDevices = useSameDevicesHelper.getRememberedDevices()
      if (rememberedDevices.isNotEmpty()) {
        return SuccessfulDeviceResult(rememberedDevices, facet, packageName)
      }

      val devices = bridge.connectedDevices()
      return if (devices.size == 1) {
        SuccessfulDeviceResult(devices, facet, packageName)
      } else if (devices.size > 1) {
        showDeviceChooserDialog(facet, packageName)
      } else {
        DeviceNotFound
      }
    }
    return null
  }

  private fun getFacet(facets: List<AndroidFacet>): AndroidFacet? {
    val appFacets =
      facets
        .map { HolderModuleGetter(it).get() }
        .mapNotNull { it.androidFacet }
        .filter { it.configuration.isAppProject }
        .distinct()

    return if (appFacets.size > 1) {
      ModuleChooserDialogHelper.showDialogForFacets(project, appFacets)
    } else {
      appFacets[0]
    }
  }

  // Can be removed after 242.23339
  class HolderModuleGetter(private val facet: AndroidFacet) : BackwardCompatibleGetter<Module>() {
    override fun getCurrentImplementation(): Module {
      return facet.module.getHolderModule()
    }

    override fun getPreviousImplementation(): Module {
      return Reflect.onClass("com.android.tools.idea.projectsystem.ModuleSystemUtil")
        .call("getHolderModule", facet.module)
        .get<ModuleImpl>()
    }
  }

  private fun showDeviceChooserDialog(facet: AndroidFacet, packageName: String): DeviceResult {
    val chooser = DeviceChooserDialog(facet)
    chooser.show()

    if (chooser.exitCode != DialogWrapper.OK_EXIT_CODE) {
      return DeviceResult.Cancelled
    }

    val selectedDevices = chooser.selectedDevices

    if (chooser.useSameDevices()) {
      useSameDevicesHelper.rememberDevices()
    }

    if (selectedDevices.isEmpty()) {
      return DeviceResult.Cancelled
    }

    return SuccessfulDeviceResult(selectedDevices.asList(), facet, packageName)
  }
}

sealed class DeviceResult {
  data class SuccessfulDeviceResult(
    val devices: List<IDevice>,
    val facet: AndroidFacet,
    val packageName: String,
  ) : DeviceResult()

  data object Cancelled : DeviceResult()

  data object DeviceNotFound : DeviceResult()
}
