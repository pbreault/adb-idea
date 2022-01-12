package com.developerphil.adbidea.adb

import com.android.ddmlib.IDevice
import com.android.tools.idea.gradle.project.model.AndroidModuleModel
import com.developerphil.adbidea.ui.DeviceChooserDialog
import com.developerphil.adbidea.ui.ModuleChooserDialogHelper
import com.developerphil.adbidea.ui.NotificationHelper
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidUtils


class DeviceResultFetcher constructor(private val project: Project, private val useSameDevicesHelper: UseSameDevicesHelper, private val bridge: Bridge) {

    fun fetch(dev: IDevice? = null): DeviceResult? {
        val facets = AndroidUtils.getApplicationFacets(project)
        if (facets.isNotEmpty()) {
            val facet = getFacet(facets) ?: return null
            val packageName = AndroidModuleModel.get(facet)?.applicationId ?: return null

            if (!bridge.isReady()) {
                NotificationHelper.error("No platform configured")
                return null
            }

            if (dev != null) {
                return DeviceResult(listOf(dev), facet, packageName)
            }

            val rememberedDevices = useSameDevicesHelper.getRememberedDevices()
            if (rememberedDevices.isNotEmpty()) {
                return DeviceResult(rememberedDevices, facet, packageName)
            }

            val devices = bridge.connectedDevices()
            return when {
                devices.size == 1 -> DeviceResult(devices, facet, packageName)
                devices.size > 1 -> showDeviceChooserDialog(facet, packageName)
                else -> null
            }
        }
        return null
    }

    private fun getFacet(facets: List<AndroidFacet>): AndroidFacet? {
        val facet: AndroidFacet?
        if (facets.size > 1) {
            facet = ModuleChooserDialogHelper.showDialogForFacets(project, facets, false)
            if (facet == null) {
                return null
            }
        } else {
            facet = facets[0]
        }
        return facet
    }


    private fun showDeviceChooserDialog(facet: AndroidFacet, packageName: String): DeviceResult? {
        val chooser = DeviceChooserDialog(facet)
        chooser.show()

        if (chooser.exitCode != DialogWrapper.OK_EXIT_CODE) {
            return null
        }


        val selectedDevices = chooser.selectedDevices

        if (chooser.useSameDevices()) {
            useSameDevicesHelper.rememberDevices()
        }

        if (selectedDevices.isEmpty()) {
            return null
        }

        return DeviceResult(selectedDevices.asList(), facet, packageName)
    }


}


data class DeviceResult(val devices: List<IDevice>, val facet: AndroidFacet, val packageName: String)