package com.developerphil.adbidea.ui

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.ObjectGraph
import com.developerphil.adbidea.preference.ProjectPreferences
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.Disposer
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidBundle
import org.joor.Reflect
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Programmatic UI for the device chooser dialog. Replaces the previous UI Designer-based
 * approach to avoid initialization order issues across IDE versions.
 */
class DeviceChooserDialog(facet: AndroidFacet) : DialogWrapper(facet.module.project, true) {

    private val myProject: Project = facet.module.project
    private val projectPreferences: ProjectPreferences =
        myProject.getService(ObjectGraph::class.java).projectPreferences

    private val rootPanel = JPanel(BorderLayout())
    private val deviceChooserWrapper = JPanel(BorderLayout())
    private val useSameDeviceCheckBox = JCheckBox("Use same device(s) for future commands")

    private val myDeviceChooser: MyDeviceChooser

    val selectedDevices: Array<IDevice>
        get() = myDeviceChooser.selectedDevices

    init {
        title = AndroidBundle.message("choose.device.dialog.title")
        okAction.isEnabled = false

        myDeviceChooser = MyDeviceChooser(true, okAction, facet, null)
        Disposer.register(myDisposable, myDeviceChooser)
        myDeviceChooser.addListener(object : DeviceChooserListener {
            override fun selectedDevicesChanged() {
                updateOkButton()
            }
        })

        // Build static UI
        rootPanel.add(deviceChooserWrapper, BorderLayout.CENTER)
        rootPanel.add(useSameDeviceCheckBox, BorderLayout.SOUTH)

        // Initialize DialogWrapper and dynamic content
        init()
        deviceChooserWrapper.add(myDeviceChooser.panel, BorderLayout.CENTER)
        myDeviceChooser.init(projectPreferences.getSelectedDeviceSerials())
        updateOkButton()
    }

    private fun persistSelectedSerialsToPreferences() {
        projectPreferences.saveSelectedDeviceSerials(
            myDeviceChooser.selectedDevices.map { it.serialNumber }.toList()
        )
    }

    private fun updateOkButton() {
        okAction.isEnabled = selectedDevices.isNotEmpty()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return try {
            myDeviceChooser.preferredFocusComponent
        } catch (e: NoSuchMethodError) { // preview versions fallback
            Reflect.on(myDeviceChooser).call("getDeviceTable").get<JComponent>()
        }
    }

    override fun doOKAction() {
        myDeviceChooser.finish()
        persistSelectedSerialsToPreferences()
        super.doOKAction()
    }

    override fun getDimensionServiceKey() = javaClass.canonicalName

    override fun createCenterPanel(): JComponent = rootPanel

    fun useSameDevices() = useSameDeviceCheckBox.isSelected
}