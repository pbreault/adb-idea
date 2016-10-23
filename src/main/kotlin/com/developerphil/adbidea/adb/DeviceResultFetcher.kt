package com.developerphil.adbidea.adb

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.ui.DeviceChooserDialog
import com.developerphil.adbidea.ui.ModuleChooserDialogHelper
import com.developerphil.adbidea.ui.NotificationHelper
import com.google.common.collect.Lists
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidUtils


class DeviceResultFetcher constructor(private val project: Project, private val useSameDevicesHelper: UseSameDevicesHelper, private val bridge: Bridge) {

    fun fetch(): DeviceResult? {
        val facets = getApplicationFacets(project)
        if (!facets.isEmpty()) {
            val facet = getFacet(facets) ?: return null
            val packageName = AdbUtil.computePackageName(facet) ?: return null

            if (!bridge.isReady()) {
                NotificationHelper.error("No platform configured")
                return null
            }

            val rememberedDevices = useSameDevicesHelper.getRememberedDevices()
            if (rememberedDevices.isNotEmpty()) {
                return DeviceResult(rememberedDevices, facet, packageName)
            }

            val devices = bridge.connectedDevices()
            if (devices.size == 1) {
                return DeviceResult(devices, facet, packageName)
            } else if (devices.size > 1) {
                return showDeviceChooserDialog(facet, packageName)
            } else {
                return null
            }
        }
        return null
    }

    private fun getFacet(facets: List<AndroidFacet>): AndroidFacet? {
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

    private fun getApplicationFacets(project: Project): List<AndroidFacet> {

        val facets = Lists.newArrayList<AndroidFacet>()
        for (facet in AndroidUtils.getApplicationFacets(project)) {
            if (!isTestProject(facet)) {
                facets.add(facet)
            }
        }

        return facets
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

        if (selectedDevices.size == 0) {
            return null
        }

        return DeviceResult(selectedDevices.asList(), facet, packageName)
    }

    private fun isTestProject(facet: AndroidFacet): Boolean {
        return facet.manifest != null
                && facet.manifest!!.instrumentations != null
                && !facet.manifest!!.instrumentations.isEmpty()
    }

}


data class DeviceResult(val devices: List<IDevice>, val facet: AndroidFacet, val packageName: String)