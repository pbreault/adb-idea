package com.developerphil.adbidea.ui;

import com.android.ddmlib.IDevice;
import com.developerphil.adbidea.ObjectGraph;
import com.developerphil.adbidea.PluginPreferences;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.stream.Collectors;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NotNull;
import org.joor.Reflect;

import static java.util.Arrays.stream;

public class DeviceChooserDialog extends DialogWrapper {
    private final Project myProject;
    private final MyDeviceChooser myDeviceChooser;

    private JPanel myPanel;
    private JPanel myDeviceChooserWrapper;
    private JCheckBox useSameDeviceSCheckBox;

    PluginPreferences pluginPreferences;

    public DeviceChooserDialog(@NotNull final AndroidFacet facet) {
        super(facet.getModule().getProject(), true);
        setTitle(AndroidBundle.message("choose.device.dialog.title"));

        myProject = facet.getModule().getProject();
        pluginPreferences = myProject.getComponent(ObjectGraph.class).getPluginPreferences();

        getOKAction().setEnabled(false);

        myDeviceChooser = new MyDeviceChooser(true, getOKAction(), facet, null);
        Disposer.register(myDisposable, myDeviceChooser);
        myDeviceChooser.addListener(this :: updateOkButton);

        myDeviceChooserWrapper.add(myDeviceChooser.getPanel());

        myDeviceChooser.init(pluginPreferences.getSelectedDeviceSerials());

        init();

        updateEnabled();
    }

    private void persistSelectedSerialsToPreferences() {
        pluginPreferences.saveSelectedDeviceSerials(stream(myDeviceChooser.getSelectedDevices()).map(IDevice :: getSerialNumber).collect(Collectors.toList()));
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

    public boolean useSameDevices() {
        return useSameDeviceSCheckBox.isSelected();
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

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        myPanel = new JPanel();
        myPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        myDeviceChooserWrapper = new JPanel();
        myDeviceChooserWrapper.setLayout(new BorderLayout(0, 0));
        myPanel.add(myDeviceChooserWrapper,
            new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        useSameDeviceSCheckBox = new JCheckBox();
        useSameDeviceSCheckBox.setBorderPainted(true);
        useSameDeviceSCheckBox.setMargin(new Insets(1, 1, 1, 1));
        useSameDeviceSCheckBox.setMaximumSize(new Dimension(280, 25));
        useSameDeviceSCheckBox.setMinimumSize(new Dimension(280, 25));
        useSameDeviceSCheckBox.setPreferredSize(new Dimension(280, 25));
        useSameDeviceSCheckBox.setText("Use same device(s) for future commands");
        useSameDeviceSCheckBox.setVerticalAlignment(3);
        myDeviceChooserWrapper.add(useSameDeviceSCheckBox, BorderLayout.SOUTH);
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return myPanel;
    }
}