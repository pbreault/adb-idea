package com.developerphil.adbidea.ui;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.ObjectGraph;
import com.developerphil.adbidea.PluginPreferences;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NotNull;
import org.joor.Reflect;

import javax.inject.Inject;
import javax.swing.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class DeviceChooserDialog extends DialogWrapper {
    private final Project myProject;
    private final MyDeviceChooser myDeviceChooser;

    private JPanel myPanel;
    private JPanel myDeviceChooserWrapper;

    @Inject
    PluginPreferences pluginPreferences;

    public DeviceChooserDialog(@NotNull final AndroidFacet facet) {
        super(facet.getModule().getProject(), true);
        setTitle(AndroidBundle.message("choose.device.dialog.title"));

        myProject = facet.getModule().getProject();
        myProject.getComponent(ObjectGraph.class).inject(this);

        getOKAction().setEnabled(false);

        myDeviceChooser = new MyDeviceChooser(true, getOKAction(), facet, facet.getConfiguration().getAndroidTarget(), null);
        Disposer.register(myDisposable, myDeviceChooser);
        myDeviceChooser.addListener(this::updateOkButton);

        myDeviceChooserWrapper.add(myDeviceChooser.getPanel());

        myDeviceChooser.init(pluginPreferences.getSelectedDeviceSerials());

        init();

        updateEnabled();
    }

    private void persistSelectedSerialsToPreferences() {
        pluginPreferences.saveSelectedDeviceSerials(
                stream(myDeviceChooser.getSelectedDevices())
                        .map(IDevice::getSerialNumber)
                        .collect(Collectors.toList()));
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