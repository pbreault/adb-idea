/*
 * Copyright 2000-2010 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.developerphil.adbidea.ui

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.IDevice.HardwareFeature
import com.android.tools.idea.run.ConnectedAndroidDevice
import com.android.tools.idea.run.LaunchCompatibility
import com.android.tools.idea.run.LaunchCompatibility.State
import com.android.tools.idea.run.LaunchCompatibilityCheckerImpl
import com.developerphil.adbidea.compatibility.BackwardCompatibleGetter
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.ColoredTableCellRenderer
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.table.JBTable
import com.intellij.util.Alarm
import com.intellij.util.containers.ContainerUtil
import gnu.trove.TIntArrayList
import org.jetbrains.android.dom.manifest.UsesFeature
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.sdk.AndroidSdkUtils
import org.joor.Reflect
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel

/**
 * @author Eugene.Kudelevsky
 *
 * https://android.googlesource.com/platform/tools/adt/idea/+/refs/heads/mirror-goog-studio-master-dev/android/src/com/android/tools/idea/run/DeviceChooser.java
 */
class MyDeviceChooser(
    multipleSelection: Boolean,
    okAction: Action,
    private val myFacet: AndroidFacet,
    private val myFilter: Condition<IDevice>?
) : Disposable {
    private val myListeners = ContainerUtil.createLockFreeCopyOnWriteList<DeviceChooserListener>()
    private val myRefreshingAlarm: Alarm
    private val myBridge: AndroidDebugBridge?

    @Volatile
    private var myProcessSelectionFlag = true

    /** The current list of devices that is displayed in the table.  */
    private var myDisplayedDevices = EMPTY_DEVICE_ARRAY

    /**
     * The current list of devices obtained from the debug bridge. This is updated in a background thread.
     * If it is different than [.myDisplayedDevices], then a [.refreshTable] invocation in the EDT thread
     * will update the displayed list to match the detected list.
     */
    private val myDetectedDevicesRef = AtomicReference(EMPTY_DEVICE_ARRAY)
    private val myPanel: JComponent
    private val myDeviceTable: JBTable
    private var mySelectedRows: IntArray? = null
    private var hadUserInteraction = false
    private var previouslySelectedSerials: Array<String>? = null
    private fun setColumnWidth(deviceTable: JBTable, columnIndex: Int, sampleText: String) {
        val width = getWidth(deviceTable, sampleText)
        deviceTable.columnModel.getColumn(columnIndex).preferredWidth = width
    }

    private fun getWidth(deviceTable: JBTable, sampleText: String): Int {
        val metrics = deviceTable.getFontMetrics(deviceTable.font)
        return metrics.stringWidth(sampleText)
    }

    fun init(selectedSerials: Array<String>?) {
        previouslySelectedSerials = selectedSerials
        updateTable()
        addUpdatingRequest()
    }

    fun init(selectedSerials: List<String>) {
        init(selectedSerials.toTypedArray())
    }

    private fun updatePreviouslySelectedSerials() {
        if (previouslySelectedSerials != null && !hadUserInteraction) {
            resetSelection(previouslySelectedSerials!!)
        }
    }

    private val myUpdateRequest = Runnable {
        updateTable()
        addUpdatingRequest()
    }

    private fun addUpdatingRequest() {
        if (myRefreshingAlarm.isDisposed) {
            return
        }
        myRefreshingAlarm.cancelAllRequests()
        myRefreshingAlarm.addRequest(myUpdateRequest, REFRESH_INTERVAL_MS)
    }

    private fun resetSelection(selectedSerials: Array<String>) {
        val model = myDeviceTable.model as MyDeviceTableModel
        val selectedSerialsSet = mutableSetOf<String>()
        Collections.addAll(selectedSerialsSet, *selectedSerials)
        val myDevices = model.myDevices
        val selectionModel = myDeviceTable.selectionModel
        var cleared = false
        var i = 0
        val n = myDevices.size
        while (i < n) {
            val serialNumber = myDevices[i].serialNumber
            if (selectedSerialsSet.contains(serialNumber)) {
                if (!cleared) {
                    selectionModel.clearSelection()
                    cleared = true
                }
                selectionModel.addSelectionInterval(i, i)
            }
            i++
        }
    }

    fun updateTable() {
        val devices = myBridge?.let { getFilteredDevices(it) } ?: EMPTY_DEVICE_ARRAY
        if (devices.size > 1) { // sort by API level
            Arrays.sort(devices, object : Comparator<IDevice> {
                override fun compare(device1: IDevice, device2: IDevice): Int {
                    val apiLevel1 = safeGetApiLevel(device1)
                    val apiLevel2 = safeGetApiLevel(device2)
                    return apiLevel2 - apiLevel1
                }

                private fun safeGetApiLevel(device: IDevice): Int {
                    return try {
                        val s = device.getProperty(IDevice.PROP_BUILD_API_LEVEL)
                        if (StringUtil.isNotEmpty(s)) s.toInt() else 0
                    } catch (e: Exception) {
                        0
                    }
                }
            })
        }
        if (!Arrays.equals(myDisplayedDevices, devices)) {
            myDetectedDevicesRef.set(devices)
            ApplicationManager.getApplication()
                .invokeLater({ refreshTable() }, ModalityState.stateForComponent(myDeviceTable))
        }
    }

    private fun refreshTable() {
        val devices = myDetectedDevicesRef.get()
        myDisplayedDevices = devices
        val selectedDevices = selectedDevices
        val selectedRows = TIntArrayList()
        for (i in devices.indices) {
            if (selectedDevices.contains(devices[i])) {
                selectedRows.add(i)
            }
        }
        myProcessSelectionFlag = false
        myDeviceTable.setModel(MyDeviceTableModel(devices))
        if (selectedRows.size() == 0 && devices.size > 0) {
            myDeviceTable.selectionModel.setSelectionInterval(0, 0)
        }
        for (selectedRow in selectedRows.toNativeArray()) {
            if (selectedRow < devices.size) {
                myDeviceTable.selectionModel.addSelectionInterval(selectedRow, selectedRow)
            }
        }
        fireSelectedDevicesChanged()
        myProcessSelectionFlag = true
        updatePreviouslySelectedSerials()
    }

    fun hasDevices(): Boolean {
        return myDetectedDevicesRef.get().isNotEmpty()
    }

    val preferredFocusComponent: JComponent
        get() = myDeviceTable

    val panel: JComponent?
        get() = myPanel

    val selectedDevices: Array<IDevice>
        get() {
            val rows = if (mySelectedRows != null) mySelectedRows!! else myDeviceTable.selectedRows
            val result = mutableListOf<IDevice>()
            for (row in rows) {
                if (row >= 0) {
                    val serial = myDeviceTable.getValueAt(row, SERIAL_COLUMN_INDEX)
                    val bridge = AndroidSdkUtils.getDebugBridge(myFacet.module.project) ?: return EMPTY_DEVICE_ARRAY
                    val devices = getFilteredDevices(bridge)
                    for (device in devices) {
                        if (device.serialNumber == serial.toString()) {
                            result.add(device)
                            break
                        }
                    }
                }
            }
            return result.toTypedArray()
        }

    private fun getFilteredDevices(bridge: AndroidDebugBridge): Array<IDevice> {
        val filteredDevices: MutableList<IDevice> = ArrayList()
        for (device in bridge.devices) {
            if (myFilter == null || myFilter.value(device)) {
                filteredDevices.add(device)
            }
        }
        // Do not filter launching cloud devices as they are just unselectable progress markers
// that are replaced with the actual cloud devices as soon as they are up and the actual cloud devices will be filtered above.
        return filteredDevices.toTypedArray()
    }

    fun finish() {
        mySelectedRows = myDeviceTable.selectedRows
    }

    override fun dispose() {}
    fun setEnabled(enabled: Boolean) {
        myDeviceTable.isEnabled = enabled
    }

    fun fireSelectedDevicesChanged() {
        for (listener in myListeners) {
            listener.selectedDevicesChanged()
        }
    }

    fun addListener(listener: DeviceChooserListener) {
        myListeners.add(listener)
    }

    private inner class MyDeviceTableModel(val myDevices: Array<IDevice>) : AbstractTableModel() {
        override fun getColumnName(column: Int): String {
            return COLUMN_TITLES[column]
        }

        override fun getRowCount(): Int {
            return myDevices.size
        }

        override fun getColumnCount(): Int {
            return COLUMN_TITLES.size
        }

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
            if (rowIndex >= myDevices.size) {
                return null
            }
            val device = myDevices[rowIndex]
            when (columnIndex) {
                DEVICE_NAME_COLUMN_INDEX -> return generateDeviceName(device)
                SERIAL_COLUMN_INDEX -> return device.serialNumber
                DEVICE_STATE_COLUMN_INDEX -> return getDeviceState(device)
                COMPATIBILITY_COLUMN_INDEX -> return LaunchCompatibilityCheckerImpl.create(myFacet, null, null)!!
                    .validate(ConnectedAndroidDeviceBuilder(device).get())
            }
            return null
        }

        private fun generateDeviceName(device: IDevice): String {
            return device.name
                .replace(device.serialNumber, "")
                .replace("[-_]".toRegex(), " ")
                .replace("[\\[\\]]".toRegex(), "")
        }

        override fun getColumnClass(columnIndex: Int): Class<*> {
            return if (columnIndex == COMPATIBILITY_COLUMN_INDEX) {
                LaunchCompatibility::class.java
            } else if (columnIndex == DEVICE_NAME_COLUMN_INDEX) {
                IDevice::class.java
            } else {
                String::class.java
            }
        }

    }

    private class LaunchCompatibilityRenderer : ColoredTableCellRenderer() {
        override fun customizeCellRenderer(
            table: JTable,
            value: Any?,
            selected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ) {
            try {
                if (value !is LaunchCompatibility) {
                    return
                }
                val compatible = value.state
                if (compatible == State.OK) {
                    append("Yes")
                } else {
                    if (compatible == State.ERROR) {
                        append("No", SimpleTextAttributes.ERROR_ATTRIBUTES)
                    } else {
                        append("Maybe")
                    }
                    val reason = value.reason
                    if (reason != null) {
                        append(", ")
                        append(reason)
                    }
                }
            } catch (e: Error) {
                // Expected on Intellij 2021.2.
                // Should be removed once the android plugin is upgraded to 7.0
            }
        }
    }

    companion object {
        private val COLUMN_TITLES = arrayOf("Device", "Serial Number", "State", "Compatible")
        private const val DEVICE_NAME_COLUMN_INDEX = 0
        private const val SERIAL_COLUMN_INDEX = 1
        private const val DEVICE_STATE_COLUMN_INDEX = 2
        private const val COMPATIBILITY_COLUMN_INDEX = 3
        private const val REFRESH_INTERVAL_MS = 500
        val EMPTY_DEVICE_ARRAY = arrayOf<IDevice>()
        private fun getRequiredHardwareFeatures(requiredFeatures: List<UsesFeature>): EnumSet<HardwareFeature> { // Currently, this method is hardcoded to only search if the list of required features includes a watch.
// We may not want to search the device for every possible feature, but only a small subset of important
// features, starting with hardware type watch..
            for (feature in requiredFeatures) {
                val name = feature.name
                if (name != null && UsesFeature.HARDWARE_TYPE_WATCH == name.stringValue) {
                    return EnumSet.of(HardwareFeature.WATCH)
                }
            }
            return EnumSet.noneOf(HardwareFeature::class.java)
        }

        @OptIn(ExperimentalStdlibApi::class)
        private fun getDeviceState(device: IDevice): String {
            val state = device.state
            return if (state != null) StringUtil.capitalize(state.name.lowercase(Locale.getDefault())) else ""
        }
    }

    init {
        myDeviceTable = JBTable()
        myPanel = ScrollPaneFactory.createScrollPane(myDeviceTable)
        myPanel.preferredSize = Dimension(450, 220)
        myDeviceTable.model = MyDeviceTableModel(EMPTY_DEVICE_ARRAY)
        myDeviceTable.setSelectionMode(if (multipleSelection) ListSelectionModel.MULTIPLE_INTERVAL_SELECTION else ListSelectionModel.SINGLE_SELECTION)
        myDeviceTable.selectionModel.addListSelectionListener {
            if (myProcessSelectionFlag) {
                hadUserInteraction = true
                fireSelectedDevicesChanged()
            }
        }
        object : DoubleClickListener() {
            override fun onDoubleClick(e: MouseEvent): Boolean {
                if (myDeviceTable.isEnabled && okAction.isEnabled) {
                    okAction.actionPerformed(null)
                    return true
                }
                return false
            }
        }.installOn(myDeviceTable)
        myDeviceTable.setDefaultRenderer(LaunchCompatibility::class.java, LaunchCompatibilityRenderer())
        myDeviceTable.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER && okAction.isEnabled) {
                    okAction.actionPerformed(null)
                }
            }
        })
        setColumnWidth(myDeviceTable, DEVICE_NAME_COLUMN_INDEX, "Samsung Galaxy Nexus Android 4.1 (API 17)")
        setColumnWidth(myDeviceTable, SERIAL_COLUMN_INDEX, "0000-0000-00000")
        setColumnWidth(myDeviceTable, DEVICE_STATE_COLUMN_INDEX, "offline")
        setColumnWidth(myDeviceTable, COMPATIBILITY_COLUMN_INDEX, "yes")
        // Do not recreate columns on every model update - this should help maintain the column sizes set above
        myDeviceTable.autoCreateColumnsFromModel = false
        // Allow sorting by columns (in lexicographic order)
        myDeviceTable.autoCreateRowSorter = true
        myRefreshingAlarm = Alarm(Alarm.ThreadToUse.POOLED_THREAD, this)
        myBridge = AndroidSdkUtils.getDebugBridge(myFacet.module.project)
    }
}


// To remove when IntelliJ merges Android Plugin 7.1
class ConnectedAndroidDeviceBuilder(
    private val device: IDevice,
) : BackwardCompatibleGetter<ConnectedAndroidDevice>() {
    override fun getCurrentImplementation() = ConnectedAndroidDevice(device)

    // On agp 7.0, there is a second nullable parameter in the constructor
    override fun getPreviousImplementation(): ConnectedAndroidDevice =
        Reflect.onClass(ConnectedAndroidDevice::class.java).create(device, null).get()
}
