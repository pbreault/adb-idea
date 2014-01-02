package com.developerphil.adbidea.ui;
/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

import com.android.ddmlib.IDevice;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.run.DeviceChooser;
import org.jetbrains.android.run.DeviceChooserListener;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Eugene.Kudelevsky
 */
public class DeviceChooserDialog extends DialogWrapper {
    private final Project myProject;
    private final DeviceChooser myDeviceChooser;

    private JPanel myPanel;
    private JPanel myDeviceChooserWrapper;

    @NonNls
    private static final String SELECTED_SERIALS_PROPERTY = "ANDROID_EXTENDED_DEVICE_CHOOSER_SERIALS";

    public DeviceChooserDialog(@NotNull final AndroidFacet facet, boolean multipleSelection) {
        super(facet.getModule().getProject(), true);
        setTitle(AndroidBundle.message("choose.device.dialog.title"));

        myProject = facet.getModule().getProject();
        final PropertiesComponent properties = PropertiesComponent.getInstance(myProject);

        final String[] selectedSerials;
        final String serialsStr = properties.getValue(SELECTED_SERIALS_PROPERTY);
        if (serialsStr != null) {
            selectedSerials = serialsStr.split(" ");
        } else {
            selectedSerials = null;
        }

        getOKAction().setEnabled(false);

        myDeviceChooser = new DeviceChooser(multipleSelection, getOKAction(), facet, null);
        Disposer.register(myDisposable, myDeviceChooser);
        myDeviceChooser.addListener(new DeviceChooserListener() {
            @Override
            public void selectedDevicesChanged() {
                updateOkButton();
            }
        });

        myDeviceChooserWrapper.add(myDeviceChooser.getPanel());

        final ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEnabled();
            }
        };

        init();

        myDeviceChooser.init(selectedSerials);

        updateEnabled();
    }

    private void updateOkButton() {
        getOKAction().setEnabled(getSelectedDevices().length > 0);
    }

    private void updateEnabled() {
        updateOkButton();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return myDeviceChooser.getDeviceTable();
    }

    @Override
    protected void doOKAction() {
        myDeviceChooser.finish();

        final PropertiesComponent properties = PropertiesComponent.getInstance(myProject);
//        properties.setValue(SELECTED_SERIALS_PROPERTY, AndroidRunningState.toString(myDeviceChooser.getSelectedDevices()));

        super.doOKAction();
    }

    @Override
    protected String getDimensionServiceKey() {
        return getClass().getCanonicalName();
    }

    @Override
    protected JComponent createCenterPanel() {
        return myPanel;
    }

    @NotNull
    public IDevice[] getSelectedDevices() {
        return myDeviceChooser.getSelectedDevices();
    }

}