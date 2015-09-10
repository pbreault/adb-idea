package com.draekko.adbtools.ui;

import com.android.ddmlib.IDevice;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.run.DeviceChooser;
import org.jetbrains.android.run.DeviceChooserListener;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.joor.Reflect;

import javax.swing.*;

public class DeviceChooserDialog extends DialogWrapper {
    private final Project myProject;
    private final DeviceChooser myDeviceChooser;

    private JPanel myPanel;
    private JPanel myDeviceChooserWrapper;

    @NonNls
    private static final String SELECTED_SERIALS_PROPERTY = DeviceChooserDialog.class.getCanonicalName() + "-SELECTED_DEVICES";

    public DeviceChooserDialog(@NotNull final AndroidFacet facet) {
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

        myDeviceChooser = newDeviceChooser(facet);
        Disposer.register(myDisposable, myDeviceChooser);
        myDeviceChooser.addListener(new DeviceChooserListener() {
            @Override
            public void selectedDevicesChanged() {
                updateOkButton();
            }
        });

        myDeviceChooserWrapper.add(myDeviceChooser.getPanel());

        init();

        myDeviceChooser.init(selectedSerials);

        updateEnabled();
    }

    private DeviceChooser newDeviceChooser(AndroidFacet facet) {
        try {
            return buildPostZeroDotSixDeviceChooser(facet);
        } catch (NoSuchMethodError e) {
            // that means that we are probably on a preview version of android studio or in intellij 13
            return buildPreZeroDotSixDeviceChooser(facet);
        }
    }

    // device chooser before android studio 0.6
    private DeviceChooser buildPreZeroDotSixDeviceChooser(AndroidFacet facet) {
        return Reflect.on(DeviceChooser.class)
                .create(true, getOKAction(), facet, new Condition<IDevice>() {
                    @Override
                    public boolean value(IDevice iDevice) {
                        return true;
                    }
                }).get();
    }

    // device chooser after android studio 0.6
    private DeviceChooser buildPostZeroDotSixDeviceChooser(AndroidFacet facet) {
        return new DeviceChooser(true, getOKAction(), facet, facet.getConfiguration().getAndroidTarget(), null);
    }

    private void updateOkButton() {
        getOKAction().setEnabled(getSelectedDevices().length > 0);
    }

    private void updateEnabled() {
        updateOkButton();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        try {
            return myDeviceChooser.getPreferredFocusComponent();
        } catch (NoSuchMethodError e) {
            // that means that we are probably on a preview version of android studio or in intellij 13
            return Reflect.on(myDeviceChooser).call("getDeviceTable").get();
        }
    }

    @Override
    protected void doOKAction() {
        myDeviceChooser.finish();

        final PropertiesComponent properties = PropertiesComponent.getInstance(myProject);
        properties.setValue(SELECTED_SERIALS_PROPERTY, toString(myDeviceChooser.getSelectedDevices()));

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

    @NotNull
    public static String toString(@NotNull IDevice[] devices) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, n = devices.length; i < n; i++) {
            builder.append(devices[i].getSerialNumber());
            if (i < n - 1) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

}