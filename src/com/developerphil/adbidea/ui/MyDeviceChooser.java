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

package com.developerphil.adbidea.ui;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.sdklib.AndroidVersion;
import com.android.sdklib.IAndroidTarget;
import com.android.tools.idea.ddms.DeviceRenderer;
import com.android.tools.idea.model.AndroidModuleInfo;
import com.android.tools.idea.model.ManifestInfo;
import com.android.tools.idea.run.LaunchCompatibility;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Alarm;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ThreeState;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashSet;
import gnu.trove.TIntArrayList;
import org.jetbrains.android.dom.AndroidAttributeValue;
import org.jetbrains.android.dom.manifest.UsesFeature;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.intellij.openapi.util.text.StringUtil.capitalize;

/**
 * @author Eugene.Kudelevsky
 */
public class MyDeviceChooser implements Disposable {
  private static final String[] COLUMN_TITLES = new String[]{"Device", "Serial Number", "State", "Compatible"};
  private static final int DEVICE_NAME_COLUMN_INDEX = 0;
  private static final int SERIAL_COLUMN_INDEX = 1;
  private static final int DEVICE_STATE_COLUMN_INDEX = 2;
  private static final int COMPATIBILITY_COLUMN_INDEX = 3;
  private static final int REFRESH_INTERVAL_MS = 500;

  public static final IDevice[] EMPTY_DEVICE_ARRAY = new IDevice[0];

  private final List<DeviceChooserListener> myListeners = ContainerUtil.createLockFreeCopyOnWriteList();
  private final Alarm myRefreshingAlarm;
  private final AndroidDebugBridge myBridge;

  private volatile boolean myProcessSelectionFlag = true;

  /** The current list of devices that is displayed in the table. */
  private IDevice[] myDisplayedDevices = EMPTY_DEVICE_ARRAY;

  /**
   * The current list of devices obtained from the debug bridge. This is updated in a background thread.
   * If it is different than {@link #myDisplayedDevices}, then a {@link #refreshTable} invocation in the EDT thread
   * will update the displayed list to match the detected list.
   */
  private AtomicReference<IDevice[]> myDetectedDevicesRef = new AtomicReference<IDevice[]>(EMPTY_DEVICE_ARRAY);

  private JComponent myPanel;
  private JBTable myDeviceTable;

  private final AndroidFacet myFacet;
  private final Condition<IDevice> myFilter;
  private final AndroidVersion myMinSdkVersion;
  private final IAndroidTarget myProjectTarget;
  private final EnumSet<IDevice.HardwareFeature> myRequiredHardwareFeatures;

  private int[] mySelectedRows;
  private boolean hadUserInteraction = false;
  @Nullable
  private String[] previouslySelectedSerials;

  public MyDeviceChooser(boolean multipleSelection,
                         @NotNull final Action okAction,
                         @NotNull AndroidFacet facet,
                         @NotNull IAndroidTarget projectTarget,
                         @Nullable Condition<IDevice> filter) {

    myFacet = facet;
    myFilter = filter;
    myMinSdkVersion = AndroidModuleInfo.get(facet).getRuntimeMinSdkVersion();
    myProjectTarget = projectTarget;
    myRequiredHardwareFeatures = getRequiredHardwareFeatures(ManifestInfo.get(facet.getModule(), true).getRequiredFeatures());

    myDeviceTable = new JBTable();
    myPanel = ScrollPaneFactory.createScrollPane(myDeviceTable);
    myPanel.setPreferredSize(new Dimension(450, 220));

    myDeviceTable.setModel(new MyDeviceTableModel(EMPTY_DEVICE_ARRAY));
    myDeviceTable.setSelectionMode(multipleSelection ?
                                   ListSelectionModel.MULTIPLE_INTERVAL_SELECTION :
                                   ListSelectionModel.SINGLE_SELECTION);
    myDeviceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (myProcessSelectionFlag) {
          hadUserInteraction = true;
          fireSelectedDevicesChanged();
        }
      }
    });
    new DoubleClickListener() {
      @Override
      protected boolean onDoubleClick(MouseEvent e) {
        if (myDeviceTable.isEnabled() && okAction.isEnabled()) {
          okAction.actionPerformed(null);
          return true;
        }
        return false;
      }
    }.installOn(myDeviceTable);

    myDeviceTable.setDefaultRenderer(LaunchCompatibility.class, new LaunchCompatibilityRenderer());
    myDeviceTable.setDefaultRenderer(IDevice.class, new DeviceRenderer.DeviceNameRenderer(facet.getAvdManagerSilently()));
    myDeviceTable.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && okAction.isEnabled()) {
          okAction.actionPerformed(null);
        }
      }
    });

    setColumnWidth(myDeviceTable, DEVICE_NAME_COLUMN_INDEX, "Samsung Galaxy Nexus Android 4.1 (API 17)");
    setColumnWidth(myDeviceTable, SERIAL_COLUMN_INDEX, "0000-0000-00000");
    setColumnWidth(myDeviceTable, DEVICE_STATE_COLUMN_INDEX, "offline");
    setColumnWidth(myDeviceTable, COMPATIBILITY_COLUMN_INDEX, "yes");

    // Do not recreate columns on every model update - this should help maintain the column sizes set above
    myDeviceTable.setAutoCreateColumnsFromModel(false);

    // Allow sorting by columns (in lexicographic order)
    myDeviceTable.setAutoCreateRowSorter(true);

    myRefreshingAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);
    myBridge = AndroidSdkUtils.getDebugBridge(myFacet.getModule().getProject());
  }

  private static EnumSet<IDevice.HardwareFeature> getRequiredHardwareFeatures(List<UsesFeature> requiredFeatures) {
    // Currently, this method is hardcoded to only search if the list of required features includes a watch.
    // We may not want to search the device for every possible feature, but only a small subset of important
    // features, starting with hardware type watch..

    for (UsesFeature feature : requiredFeatures) {
      AndroidAttributeValue<String> name = feature.getName();
      if (name != null && UsesFeature.HARDWARE_TYPE_WATCH.equals(name.getStringValue())) {
        return EnumSet.of(IDevice.HardwareFeature.WATCH);
      }
    }

    return EnumSet.noneOf(IDevice.HardwareFeature.class);
  }

  private void setColumnWidth(JBTable deviceTable, int columnIndex, String sampleText) {
    int width = getWidth(deviceTable, sampleText);
    deviceTable.getColumnModel().getColumn(columnIndex).setPreferredWidth(width);
  }

  private int getWidth(JBTable deviceTable, String sampleText) {
    FontMetrics metrics = deviceTable.getFontMetrics(deviceTable.getFont());
    return metrics.stringWidth(sampleText);
  }

  public void init(@Nullable String[] selectedSerials) {
    previouslySelectedSerials = selectedSerials;
    updateTable();
    addUpdatingRequest();
  }

  private void updatePreviouslySelectedSerials() {
    if (previouslySelectedSerials != null && !hadUserInteraction) {
      resetSelection(previouslySelectedSerials);
    }
  }

  private final Runnable myUpdateRequest = new Runnable() {
    @Override
    public void run() {
      updateTable();
      addUpdatingRequest();
    }
  };

  private void addUpdatingRequest() {
    if (myRefreshingAlarm.isDisposed()) {
      return;
    }
    myRefreshingAlarm.cancelAllRequests();
    myRefreshingAlarm.addRequest(myUpdateRequest, REFRESH_INTERVAL_MS);
  }

  private void resetSelection(@NotNull String[] selectedSerials) {
    MyDeviceTableModel model = (MyDeviceTableModel)myDeviceTable.getModel();
    Set<String> selectedSerialsSet = new HashSet<String>();
    Collections.addAll(selectedSerialsSet, selectedSerials);
    IDevice[] myDevices = model.myDevices;
    ListSelectionModel selectionModel = myDeviceTable.getSelectionModel();
    boolean cleared = false;

    for (int i = 0, n = myDevices.length; i < n; i++) {
      String serialNumber = myDevices[i].getSerialNumber();
      if (selectedSerialsSet.contains(serialNumber)) {
        if (!cleared) {
          selectionModel.clearSelection();
          cleared = true;
        }
        selectionModel.addSelectionInterval(i, i);
      }
    }
  }

  void updateTable() {
    IDevice[] devices = myBridge != null ? getFilteredDevices(myBridge) : EMPTY_DEVICE_ARRAY;
    if (devices.length > 1) {
      // sort by API level
      Arrays.sort(devices, new Comparator<IDevice>() {
        @Override
        public int compare(IDevice device1, IDevice device2) {
          int apiLevel1 = safeGetApiLevel(device1);
          int apiLevel2 = safeGetApiLevel(device2);
          return apiLevel2 - apiLevel1;
        }

        private int safeGetApiLevel(IDevice device) {
          try {
            String s = device.getProperty(IDevice.PROP_BUILD_API_LEVEL);
            return StringUtil.isNotEmpty(s) ? Integer.parseInt(s) : 0;
          } catch (Exception e) {
            return 0;
          }
        }
      });
    }

    if (!Arrays.equals(myDisplayedDevices, devices)) {
      myDetectedDevicesRef.set(devices);
      ApplicationManager.getApplication().invokeLater(new Runnable() {
        @Override
        public void run() {
          refreshTable();
        }
      }, ModalityState.stateForComponent(myDeviceTable));
    }
  }

  private void refreshTable() {
    IDevice[] devices = myDetectedDevicesRef.get();
    myDisplayedDevices = devices;

    final IDevice[] selectedDevices = getSelectedDevices();
    final TIntArrayList selectedRows = new TIntArrayList();
    for (int i = 0; i < devices.length; i++) {
      if (ArrayUtil.indexOf(selectedDevices, devices[i]) >= 0) {
        selectedRows.add(i);
      }
    }

    myProcessSelectionFlag = false;
    myDeviceTable.setModel(new MyDeviceTableModel(devices));
    if (selectedRows.size() == 0 && devices.length > 0) {
      myDeviceTable.getSelectionModel().setSelectionInterval(0, 0);
    }
    for (int selectedRow : selectedRows.toNativeArray()) {
      if (selectedRow < devices.length) {
        myDeviceTable.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
      }
    }
    fireSelectedDevicesChanged();
    myProcessSelectionFlag = true;
    updatePreviouslySelectedSerials();
  }

  public boolean hasDevices() {
    return myDetectedDevicesRef.get().length > 0;
  }

  public JComponent getPreferredFocusComponent() {
    return myDeviceTable;
  }

  @Nullable
  public JComponent getPanel() {
    return myPanel;
  }

  @NotNull
  public IDevice[] getSelectedDevices() {
    int[] rows = mySelectedRows != null ? mySelectedRows : myDeviceTable.getSelectedRows();
    List<IDevice> result = new ArrayList<IDevice>();
    for (int row : rows) {
      if (row >= 0) {
        Object serial = myDeviceTable.getValueAt(row, SERIAL_COLUMN_INDEX);
        final AndroidDebugBridge bridge = AndroidSdkUtils.getDebugBridge(myFacet.getModule().getProject());
        if (bridge == null) {
          return EMPTY_DEVICE_ARRAY;
        }
        IDevice[] devices = getFilteredDevices(bridge);
        for (IDevice device : devices) {
          if (device.getSerialNumber().equals(serial.toString())) {
            result.add(device);
            break;
          }
        }
      }
    }
    return result.toArray(new IDevice[result.size()]);
  }

  @NotNull
  private IDevice[] getFilteredDevices(AndroidDebugBridge bridge) {
    final List<IDevice> filteredDevices = new ArrayList<IDevice>();
    for (IDevice device : bridge.getDevices()) {
      if (myFilter == null || myFilter.value(device)) {
        filteredDevices.add(device);
      }
    }
    // Do not filter launching cloud devices as they are just unselectable progress markers
    // that are replaced with the actual cloud devices as soon as they are up and the actual cloud devices will be filtered above.
    return filteredDevices.toArray(new IDevice[filteredDevices.size()]);
  }

  public void finish() {
    mySelectedRows = myDeviceTable.getSelectedRows();
  }

  @Override
  public void dispose() {
  }

  public void setEnabled(boolean enabled) {
    myDeviceTable.setEnabled(enabled);
  }

  @NotNull
  private static String getDeviceState(@NotNull IDevice device) {
    IDevice.DeviceState state = device.getState();
    return state != null ? capitalize(state.name().toLowerCase()) : "";
  }

  public void fireSelectedDevicesChanged() {
    for (DeviceChooserListener listener : myListeners) {
      listener.selectedDevicesChanged();
    }
  }

  public void addListener(@NotNull DeviceChooserListener listener) {
    myListeners.add(listener);
  }

  private class MyDeviceTableModel extends AbstractTableModel {
    private final IDevice[] myDevices;

    public MyDeviceTableModel(IDevice[] devices) {
      myDevices = devices;
    }

    @Override
    public String getColumnName(int column) {
      return COLUMN_TITLES[column];
    }

    @Override
    public int getRowCount() {
      return myDevices.length;
    }

    @Override
    public int getColumnCount() {
      return COLUMN_TITLES.length;
    }

    @Override
    @Nullable
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex >= myDevices.length) {
        return null;
      }
      IDevice device = myDevices[rowIndex];
      switch (columnIndex) {
        case DEVICE_NAME_COLUMN_INDEX:
          return device;
        case SERIAL_COLUMN_INDEX:
          return device.getSerialNumber();
        case DEVICE_STATE_COLUMN_INDEX:
          return getDeviceState(device);
        case COMPATIBILITY_COLUMN_INDEX:
          return LaunchCompatibility.canRunOnDevice(myMinSdkVersion, myProjectTarget, myRequiredHardwareFeatures, device, null);
      }
      return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == COMPATIBILITY_COLUMN_INDEX) {
        return LaunchCompatibility.class;
      } else if (columnIndex == DEVICE_NAME_COLUMN_INDEX) {
        return IDevice.class;
      } else {
        return String.class;
      }
    }
  }

  private static class LaunchCompatibilityRenderer extends ColoredTableCellRenderer {
    @Override
    protected void customizeCellRenderer(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
      if (!(value instanceof LaunchCompatibility)) {
        return;
      }

      LaunchCompatibility compatibility = (LaunchCompatibility)value;
      ThreeState compatible = compatibility.isCompatible();
      if (compatible == ThreeState.YES) {
        append("Yes");
      } else {
        if (compatible == ThreeState.NO) {
          append("No", SimpleTextAttributes.ERROR_ATTRIBUTES);
        } else {
          append("Maybe");
        }
        String reason = compatibility.getReason();
        if (reason != null) {
          append(", ");
          append(reason);
        }
      }
    }
  }
}
