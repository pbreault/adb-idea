package com.developerphil.adbidea.adb

import com.android.ddmlib.IDevice
import com.android.tools.idea.insights.isAndroidApp
import com.android.tools.idea.model.AndroidModel
import com.android.tools.idea.util.androidFacet
import com.developerphil.adbidea.adb.DeviceResult.DeviceNotFound
import com.developerphil.adbidea.adb.DeviceResult.SuccessfulDeviceResult
import com.developerphil.adbidea.ui.DeviceChooserDialog
import com.developerphil.adbidea.ui.ModuleChooserDialogHelper
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.facet.ProjectFacetManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.android.facet.AndroidFacet


class DeviceResultFetcher constructor(
    private val project: Project,
    private val useSameDevicesHelper: UseSameDevicesHelper,
    private val bridge: Bridge
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
        val appFacets = facets
            .filter { it.holderModule.isAndroidApp }
            .mapNotNull { it.holderModule.androidFacet }
            .distinct()

        return if (appFacets.size > 1) {
            ModuleChooserDialogHelper.showDialogForFacets(project, appFacets)
        } else {
            appFacets[0]
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
        val packageName: String
    ) : DeviceResult()

    object Cancelled : DeviceResult()
    object DeviceNotFound : DeviceResult()
}
