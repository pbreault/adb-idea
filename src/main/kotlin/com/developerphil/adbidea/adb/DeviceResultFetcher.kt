package com.developerphil.adbidea.adb

import com.android.ddmlib.IDevice
import com.android.tools.idea.gradle.project.model.AndroidModuleModel
import com.android.tools.idea.util.androidFacet
import com.developerphil.adbidea.adb.DeviceResult.DeviceNotFound
import com.developerphil.adbidea.adb.DeviceResult.SuccessfulDeviceResult
import com.developerphil.adbidea.ui.DeviceChooserDialog
import com.developerphil.adbidea.ui.ModuleChooserDialogHelper
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidUtils


class DeviceResultFetcher constructor(
    private val project: Project,
    private val useSameDevicesHelper: UseSameDevicesHelper,
    private val bridge: Bridge
) {

    fun fetch(): DeviceResult? {
        val facets = AndroidUtils.getApplicationFacets(project)
        if (facets.isNotEmpty()) {
            val facet = getFacet(facets) ?: return null
            val packageName = AndroidModuleModel.get(facet)?.applicationId ?: return null

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

    private fun getFacet(_facets: List<AndroidFacet>): AndroidFacet? {
        val facets = _facets.mapNotNull { it.holderModule.androidFacet }.distinct()
        val facet: AndroidFacet?
        if (facets.size > 1) {
            facet = ModuleChooserDialogHelper.showDialogForFacets(project, facets)
            if (facet == null) {
                return null
            }
        } else {
            facet = facets[0]
        }

        return facet
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
