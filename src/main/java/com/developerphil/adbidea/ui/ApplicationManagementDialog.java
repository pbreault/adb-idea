package com.developerphil.adbidea.ui;

import com.developerphil.adbidea.adb.AdbFacade;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.jetbrains.annotations.Nullable;

/**
 * Created by XQ Yang on 8/28/2018  4:13 PM.
 * Description :
 */

public class ApplicationManagementDialog extends JDialog {
    private MyApplistModel mModel;
    private List<String> mList;
    private JRadioButton mAllStatusRadioButton;
    private JRadioButton mDisabledRadioButton;
    private JRadioButton mEnabledRadioButton;
    private JRadioButton mAllTypeRadioButton;
    private JRadioButton mSystemRadioButton;
    private JRadioButton mThirdPartyRadioButton;
    private JCheckBox mShowApkFileCheckBox;
    private JCheckBox mShowInstallersCheckBox;
    private JCheckBox mContainsUninstalledCheckBox;
    private JButton mQueryButton;
    private JTextField tv_keyword;
    private JList mJList;
    private JButton mUninstallButton;
    private JButton mClearAppCacheDataButton;
    private JButton mRunningServicesButton;
    private JButton mViewDetailButton;
    private JButton mViewPathButton;
    private JPanel mPanel;
    private JScrollPane sp;
    private JTextPane tp;
    private JScrollPane sp_tp;

    private static final String PARAMETER_DISABLED = "-d ";
    private static final String PARAMETER_ENABLED = "-e ";
    private static final String PARAMETER_SYSTEM = "-s ";
    private static final String PARAMETER_THIRD_PARTY = "-3 ";
    private static final String PARAMETER_INSTALLER = "-i ";
    private static final String PARAMETER_UNINSTALLED = "-u ";
    private static final String PARAMETER_RELEVANCE_FILE = "-f ";

    private Project mProject;
    private final JPopupMenu mPopupMenu;

    public ApplicationManagementDialog(@Nullable Project project) {
        setResizable(true);
        setModal(true);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 3 - getWidth() / 2;
        int y = (int) screensize.getHeight() / 3 - getHeight() / 2;
        setTitle("Adb Application Management");
        setLocation(x, y);
        mProject = project;
        mPopupMenu = new JPopupMenu();
        JMenuItem clear = mPopupMenu.add(new JMenuItem("clear"));
        clear.addActionListener(e -> tp.setText(""));
        tp.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    mPopupMenu.show(tp, e.getX(), e.getY());
                }
            }
        });
        mJList.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    mJList.clearSelection();
                }
            }
        });
        mQueryButton.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            if (mDisabledRadioButton.isSelected()) {
                sb.append(PARAMETER_DISABLED);
            } else if (mEnabledRadioButton.isSelected()) {
                sb.append(PARAMETER_ENABLED);
            }
            if (mSystemRadioButton.isSelected()) {
                sb.append(PARAMETER_SYSTEM);
            } else if (mThirdPartyRadioButton.isSelected()) {
                sb.append(PARAMETER_THIRD_PARTY);
            }
            if (mShowApkFileCheckBox.isSelected()) {
                sb.append(PARAMETER_RELEVANCE_FILE);
            }
            if (mShowInstallersCheckBox.isSelected()) {
                sb.append(PARAMETER_INSTALLER);
            }
            if (mContainsUninstalledCheckBox.isSelected()) {
                sb.append(PARAMETER_UNINSTALLED);
            }
            String keywordText = tv_keyword.getText();
            if (!Utils.isEmpty(keywordText)) {
                sb.append(keywordText);
            }
            AdbFacade.getAllApplicationList(mProject, sb.toString(), strings -> {
                Collections.sort(strings);
                mList = strings;
                mModel = new MyApplistModel(mList);
                mJList.setModel(mModel);
                return null;
            });
        });
        mUninstallButton.addActionListener(e -> {
            int[] selectedIndices = mJList.getSelectedIndices();
            String[] selected = new String[selectedIndices.length];
            for (int i = 0; i < selectedIndices.length; i++) {
                String packageName = mList.get(selectedIndices[i]);
                AdbFacade.uninstall(mProject, getRealPackageName(packageName));
                selected[i] = packageName;
            }
            for (String s : selected) {
                mModel.delete(s);
            }
        });

        mClearAppCacheDataButton.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            for (String packageName : selectedValuesList) {
                AdbFacade.clearData(mProject, getRealPackageName(packageName));
            }
        });
        mViewDetailButton.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            for (String packageName : selectedValuesList) {
                String name = getRealPackageName(packageName);
                append2Ta("View " + name + " detail : \n", JBColor.BLUE);
                AdbFacade.getPackageDetail(mProject, name, s -> {
                    append2Ta(s);
                    return null;
                });
            }
        });
        mViewPathButton.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            for (String packageName : selectedValuesList) {
                String name = getRealPackageName(packageName);
                append2Ta("View " + name + "apk path : \n", JBColor.BLUE);
                AdbFacade.getPackagePath(mProject, name, s -> {
                    append2Ta(s);
                    return null;
                });
            }
        });
        mRunningServicesButton.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            if (selectedValuesList.isEmpty()) {
                String keywordText = tv_keyword.getText();
                if (!Utils.isEmpty(keywordText)) {
                    append2Ta("View running services related with" + keywordText + " : \n", JBColor.BLUE);
                    AdbFacade.getActivityService(mProject, keywordText, s -> {
                        append2Ta(s);
                        return null;
                    });
                } else {
                    append2Ta("View all running services : \n", JBColor.BLUE);
                    AdbFacade.getActivityService(mProject, "", s -> {
                        append2Ta(s);
                        return null;
                    });
                }
            }
            for (String packageName : selectedValuesList) {
                String name = getRealPackageName(packageName);
                append2Ta("View running services related with" + name + " : \n", JBColor.BLUE);
                AdbFacade.getActivityService(mProject, name, s -> {
                    append2Ta(s);
                    return null;
                });
            }
        });
        setContentPane($$$getRootComponent$$$());
    }

    private void append2Ta(String str, Color color) {
        Document doc = tp.getDocument();
        if (doc != null) {
            try {
                MutableAttributeSet attr = null;
                if (color != null) {
                    attr = new SimpleAttributeSet();
                    StyleConstants.setForeground(attr, color);
                    StyleConstants.setBold(attr, true);
                }
                doc.insertString(doc.getLength(), str, attr);
            } catch (BadLocationException e) {
            }
        }
    }

    private void append2Ta(String str) {
        append2Ta(str, null);
    }

    private String getRealPackageName(String packageName) {
        if (mShowApkFileCheckBox.isSelected() && mShowInstallersCheckBox.isSelected()) {
            String[] split = packageName.split("=");
            return split[1].split(" ")[0];
        }
        if (mShowApkFileCheckBox.isSelected()) {
            String[] split = packageName.split("=");
            return split[1];
        }
        if (mShowInstallersCheckBox.isSelected()) {
            String[] split = packageName.split(":");
            return split[1].split(" ")[0];
        }
        String[] strings = packageName.split(":");
        return strings[1];
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
        mPanel = new JPanel();
        mPanel.setLayout(new FormLayout("fill:341px:noGrow,fill:d:grow,fill:d:noGrow,fill:d:noGrow",
            "center:max(d;4px):noGrow,center:44px:noGrow,center:32px:noGrow,center:d:noGrow,center:32px:noGrow,top:4dlu:noGrow,center:d:grow"));
        mPanel.setMinimumSize(new Dimension(590, 280));
        mPanel.setName("");
        mPanel.setPreferredSize(new Dimension(590, 280));
        mPanel.setBorder(BorderFactory.createTitledBorder("All Application"));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        CellConstraints cc = new CellConstraints();
        mPanel.add(panel1, cc.xyw(1, 2, 4));
        panel1.setBorder(BorderFactory.createTitledBorder("Other"));
        mShowApkFileCheckBox = new JCheckBox();
        mShowApkFileCheckBox.setText("Show Apk File");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(mShowApkFileCheckBox, gbc);
        mShowInstallersCheckBox = new JCheckBox();
        mShowInstallersCheckBox.setText("Show installers");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(mShowInstallersCheckBox, gbc);
        mContainsUninstalledCheckBox = new JCheckBox();
        mContainsUninstalledCheckBox.setText("Contains uninstalled");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(mContainsUninstalledCheckBox, gbc);
        mQueryButton = new JButton();
        mQueryButton.setText("Query");
        mPanel.add(mQueryButton, cc.xy(4, 3));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mPanel.add(panel2, cc.xyw(1, 3, 3));
        final JLabel label1 = new JLabel();
        label1.setText("Name Filter :");
        panel2.add(label1,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
                null, 0, false));
        tv_keyword = new JTextField();
        panel2.add(tv_keyword,
            new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(
            new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow",
                "center:d:noGrow"));
        mPanel.add(panel3, cc.xyw(1, 5, 4));
        mUninstallButton = new JButton();
        mUninstallButton.setText("Uninstall");
        panel3.add(mUninstallButton, cc.xy(1, 1));
        mClearAppCacheDataButton = new JButton();
        mClearAppCacheDataButton.setText("Clear app  cache  data");
        panel3.add(mClearAppCacheDataButton, cc.xy(3, 1));
        mRunningServicesButton = new JButton();
        mRunningServicesButton.setText("Running Services");
        panel3.add(mRunningServicesButton, cc.xy(5, 1));
        mViewDetailButton = new JButton();
        mViewDetailButton.setText("View Detail");
        panel3.add(mViewDetailButton, cc.xy(7, 1));
        mViewPathButton = new JButton();
        mViewPathButton.setText("View Path");
        panel3.add(mViewPathButton, cc.xy(9, 1));
        sp = new JScrollPane();
        sp.setMinimumSize(new Dimension(0, 100));
        mPanel.add(sp, cc.xyw(1, 4, 4, CellConstraints.FILL, CellConstraints.FILL));
        mJList = new JList();
        mJList.setMinimumSize(new Dimension(0, 100));
        sp.setViewportView(mJList);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        mPanel.add(panel4, cc.xy(1, 1));
        panel4.setBorder(BorderFactory.createTitledBorder("Type"));
        mAllTypeRadioButton = new JRadioButton();
        mAllTypeRadioButton.setSelected(true);
        mAllTypeRadioButton.setText("All");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(mAllTypeRadioButton, gbc);
        mSystemRadioButton = new JRadioButton();
        mSystemRadioButton.setText("System");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(mSystemRadioButton, gbc);
        mThirdPartyRadioButton = new JRadioButton();
        mThirdPartyRadioButton.setText("Third-party");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(mThirdPartyRadioButton, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        panel5.setToolTipText("status");
        mPanel.add(panel5, cc.xyw(2, 1, 3));
        panel5.setBorder(BorderFactory.createTitledBorder("Status"));
        mAllStatusRadioButton = new JRadioButton();
        mAllStatusRadioButton.setSelected(true);
        mAllStatusRadioButton.setText("All");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(mAllStatusRadioButton, gbc);
        mEnabledRadioButton = new JRadioButton();
        mEnabledRadioButton.setText("enabled");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(mEnabledRadioButton, gbc);
        mDisabledRadioButton = new JRadioButton();
        mDisabledRadioButton.setText("disabled");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(mDisabledRadioButton, gbc);
        sp_tp = new JScrollPane();
        mPanel.add(sp_tp, cc.xyw(1, 7, 4, CellConstraints.FILL, CellConstraints.FILL));
        tp = new JTextPane();
        sp_tp.setViewportView(tp);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(mDisabledRadioButton);
        buttonGroup.add(mAllStatusRadioButton);
        buttonGroup.add(mEnabledRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(mAllTypeRadioButton);
        buttonGroup.add(mSystemRadioButton);
        buttonGroup.add(mThirdPartyRadioButton);
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return mPanel;
    }
}
