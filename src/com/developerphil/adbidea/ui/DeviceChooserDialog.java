package com.developerphil.adbidea.ui;

import com.android.ddmlib.IDevice;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joor.Reflect;

import javax.swing.*;

public class DeviceChooserDialog extends DialogWrapper {
    private final Project myProject;
    private final MyDeviceChooser myDeviceChooser;

    private JPanel myPanel;
    private JPanel myDeviceChooserWrapper;

    @NonNls
    private static final String SELECTED_SERIALS_PROPERTY = DeviceChooserDialog.class.getCanonicalName() + "-SELECTED_DEVICES";

    public DeviceChooserDialog(@NotNull final AndroidFacet facet) {
        super(facet.getModule().getProject(), true);
        setTitle(AndroidBundle.message("choose.device.dialog.title"));

        myProject = facet.getModule().getProject();
        final PropertiesComponent properties = PropertiesComponent.getInstance(myProject);

        getOKAction().setEnabled(false);

        myDeviceChooser = new MyDeviceChooser(true, getOKAction(), facet, facet.getConfiguration().getAndroidTarget(), null);
        Disposer.register(myDisposable, myDeviceChooser);
        myDeviceChooser.addListener(new DeviceChooserListener() {
            @Override
            public void selectedDevicesChanged() {
                    updateOkButton();
            }
        });

        myDeviceChooserWrapper.add(myDeviceChooser.getPanel());

        final String[] selectedSerials = getSelectedSerialsFromPreferences(properties);
        myDeviceChooser.init(selectedSerials);

        init();

        updateEnabled();
    }

    @Nullable
    private String[] getSelectedSerialsFromPreferences(PropertiesComponent properties) {
        final String[] selectedSerials;
        final String serialsStr = properties.getValue(SELECTED_SERIALS_PROPERTY);
        if (serialsStr != null) {
            selectedSerials = serialsStr.split(" ");
        } else {
            selectedSerials = null;
        }
        return selectedSerials;
    }

    private void persistSelectedSerialsToPreferences() {
        final PropertiesComponent properties = PropertiesComponent.getInstance(myProject);
        properties.setValue(SELECTED_SERIALS_PROPERTY, toString(myDeviceChooser.getSelectedDevices()));
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
        persistSelectedSerialsToPreferences();
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