package com.developerphil.adbidea.ui

import com.android.ddmlib.IDevice
import com.developerphil.adbidea.ObjectGraph
import com.developerphil.adbidea.PluginPreferences
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.Disposer
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidBundle
import org.joor.Reflect
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

class DeviceChooserDialog(facet: AndroidFacet) : DialogWrapper(facet.module.project, true) {

    lateinit var myPanel: JPanel
    lateinit var myDeviceChooserWrapper: JPanel
    lateinit var useSameDeviceSCheckBox: JCheckBox

    private val myProject: Project
    private val myDeviceChooser: MyDeviceChooser
    private val pluginPreferences: PluginPreferences

    val selectedDevices: Array<IDevice>
        get() = myDeviceChooser.selectedDevices

    init {
        title = AndroidBundle.message("choose.device.dialog.title")
        myProject = facet.module.project
        pluginPreferences = myProject.getComponent(ObjectGraph::class.java).pluginPreferences
        okAction.isEnabled = false
        myDeviceChooser = MyDeviceChooser(true, okAction, facet, facet.configuration.androidTarget!!, null)
        Disposer.register(myDisposable, myDeviceChooser)
        myDeviceChooser.addListener(object : DeviceChooserListener {
            override fun selectedDevicesChanged() {
                updateOkButton()
            }
        })
        myDeviceChooserWrapper.add(myDeviceChooser.panel)
        myDeviceChooser.init(pluginPreferences.getSelectedDeviceSerials())
        init()
        updateOkButton()
    }

    private fun persistSelectedSerialsToPreferences() {
        pluginPreferences.saveSelectedDeviceSerials(myDeviceChooser.selectedDevices.map { it.serialNumber }.toList())
    }

    private fun updateOkButton() {
        okAction.isEnabled = selectedDevices.isNotEmpty()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return try {
            myDeviceChooser.preferredFocusComponent
        } catch (e: NoSuchMethodError) { // that means that we are probably on a preview version of android studio or in intellij 13
            Reflect.on(myDeviceChooser).call("getDeviceTable").get<JComponent>()
        }
    }

    override fun doOKAction() {
        myDeviceChooser.finish()
        persistSelectedSerialsToPreferences()
        super.doOKAction()
    }

    override fun getDimensionServiceKey() = javaClass.canonicalName
    override fun createCenterPanel(): JComponent = myPanel

    fun useSameDevices() = useSameDeviceSCheckBox.isSelected
}