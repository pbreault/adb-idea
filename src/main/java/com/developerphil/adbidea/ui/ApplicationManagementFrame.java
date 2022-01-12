package com.developerphil.adbidea.ui;

import com.developerphil.adbidea.HelperMethodsKt;
import com.developerphil.adbidea.adb.AdbFacade;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.jetbrains.annotations.Nullable;

/**
 * Created by XQ Yang on 8/28/2018  4:13 PM.
 * Description :
 */

public class ApplicationManagementFrame extends JFrame {
    private MyApplistModel mModel;
    private List<String>   mList;
    private JRadioButton   mAllStatusRadioButton;
    private JRadioButton   mDisabledRadioButton;
    private JRadioButton   mEnabledRadioButton;
    private JRadioButton   mAllTypeRadioButton;
    private JRadioButton   mSystemRadioButton;
    private JRadioButton   mThirdPartyRadioButton;
    private JCheckBox      mShowApkFileCheckBox;
    private JCheckBox      mShowInstallersCheckBox;
    private JCheckBox      mContainsUninstalledCheckBox;
    private JButton        mQueryButton;
    private JTextField     tv_keyword;
    private JList          mJList;
    private JButton        mRunningServicesButton;
    private JPanel         mPanel;
    private JScrollPane    sp;
    private JTextPane      tp;
    private JScrollPane    sp_tp;
    private JButton        mForegroundActivityButton;
    private JButton        mMonkeyTestButton;
    private JTextField     tv_search;
    private JButton        btn_search;

    private static final String PARAMETER_DISABLED       = "-d ";
    private static final String PARAMETER_ENABLED        = "-e ";
    private static final String PARAMETER_SYSTEM         = "-s ";
    private static final String PARAMETER_THIRD_PARTY    = "-3 ";
    private static final String PARAMETER_INSTALLER      = "-i ";
    private static final String PARAMETER_UNINSTALLED    = "-u ";
    private static final String PARAMETER_RELEVANCE_FILE = "-f ";

    private final Project    mProject;
    private final JPopupMenu mPopupMenu;
    private final JPopupMenu mListPopupMenu;

    public ApplicationManagementFrame(@Nullable Project project) {
        setResizable(true);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - mPanel.getPreferredSize().width / 2;
        int y = (int) screensize.getHeight() / 2 - mPanel.getPreferredSize().height / 2;
        setTitle("Adb Application Management");
        URL filename = getClass().getResource("/icon.png");
        ImageIcon icon = new ImageIcon(filename);
        setIconImage(icon.getImage());
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
        mListPopupMenu = new JPopupMenu();
        JMenuItem pullApk = mListPopupMenu.add(new JMenuItem("pull apk file to ..."));
        pullApk.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            if (!selectedValuesList.isEmpty()) {
                FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
                descriptor.setTitle("Select apk file save to ... ");
                VirtualFile[] choose = new FileChooserDialogImpl(descriptor, project).choose(project);
                if (choose.length > 0 && choose[0] != null) {
                    VirtualFile selectedFile = choose[0];
                    for (String sv : selectedValuesList) {
                        String name = getRealPackageName(sv);
                        AdbFacade.INSTANCE.getPackagePath(mProject, name, s -> {
                            String realPath = s.replace("package:", "").replace("\n", "").replace("\r", "");
                            NotificationHelper.INSTANCE.info(String.format("<b>%s</b>  package path = %s", name, realPath));
                            String fileName = name + ".apk";
                            NotificationHelper.INSTANCE.info(String.format("start pull %s to %s", fileName, selectedFile.getCanonicalPath()));
                            SwingUtilities.invokeLater(() -> {
                                File file = new File(selectedFile.getCanonicalPath(), fileName);
                                AdbFacade.INSTANCE.pullFile(project, realPath, file, false);
                                NotificationHelper.INSTANCE.info(String.format("pull %s success ", fileName));
                            });
                            return null;
                        });
                    }
                }
            }
        });
        JMenuItem jiUninstall = mListPopupMenu.add(new JMenuItem("uninstall"));
        jiUninstall.addActionListener(e -> {
            int[] selectedIndices = mJList.getSelectedIndices();
            String[] selected = new String[selectedIndices.length];
            for (int i = 0; i < selectedIndices.length; i++) {
                String packageName = mList.get(selectedIndices[i]);
                AdbFacade.INSTANCE.uninstall(mProject, getRealPackageName(packageName));
                selected[i] = packageName;
            }
            for (String s : selected) {
                mModel.delete(s);
            }
        });
        JMenuItem jiClear = mListPopupMenu.add(new JMenuItem("clear data and cache"));
        jiClear.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            for (String packageName : selectedValuesList) {
                AdbFacade.INSTANCE.clearData(mProject, getRealPackageName(packageName));
            }
        });
        JMenuItem jiDetail = mListPopupMenu.add(new JMenuItem("view detail"));
        jiDetail.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            for (String packageName : selectedValuesList) {
                String name = getRealPackageName(packageName);
                Utils.Companion.append2TextPane("View " + name + " detail : \n", JBColor.BLUE, tp);
                AdbFacade.INSTANCE.getPackageDetail(mProject, name, s -> {
                    Utils.Companion.append2TextPane(s, tp);
                    return null;
                });
            }
        });
        JMenuItem jiPath = mListPopupMenu.add(new JMenuItem("view apk path"));
        jiPath.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            for (String packageName : selectedValuesList) {
                String name = getRealPackageName(packageName);
                Utils.Companion.append2TextPane("View " + name + " apk Path : \n", JBColor.BLUE, tp);
                AdbFacade.INSTANCE.getPackagePath(mProject, name, s -> {
                    Utils.Companion.append2TextPane(s, tp);
                    return null;
                });
            }
        });
        JMenuItem jiStop = mListPopupMenu.add(new JMenuItem("force stop"));
        jiStop.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            for (String packageName : selectedValuesList) {
                String name = getRealPackageName(packageName);
                Utils.Companion.append2TextPane("Force-stop : " + name + "\n", JBColor.BLUE, tp);
                AdbFacade.INSTANCE.forceStop(mProject, name);
            }
        });

        mJList.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    //mJList.clearSelection();
                    mListPopupMenu.show(mJList, e.getX(), e.getY());
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
            if (!Utils.Companion.isEmpty(keywordText)) {
                sb.append(keywordText);
            }
            AdbFacade.INSTANCE.getAllApplicationList(mProject, sb.toString(), strings -> {
                Collections.sort(strings);
                mList = strings;
                mModel = new MyApplistModel(mList);
                mJList.setModel(mModel);
                return null;
            });
        });

        mRunningServicesButton.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            if (selectedValuesList.isEmpty()) {
                String keywordText = tv_keyword.getText();
                if (!Utils.Companion.isEmpty(keywordText)) {
                    Utils.Companion.append2TextPane("View running services related with " + keywordText + " : \n", JBColor.BLUE, tp);
                    AdbFacade.INSTANCE.getActivityService(mProject, keywordText, s -> {
                        Utils.Companion.append2TextPane(s, tp);
                        return null;
                    });
                } else {
                    Utils.Companion.append2TextPane("View all running services : \n", JBColor.BLUE, tp);
                    AdbFacade.INSTANCE.getActivityService(mProject, "", s -> {
                        Utils.Companion.append2TextPane(s, tp);
                        return null;
                    });
                }
            }
            for (String packageName : selectedValuesList) {
                String name = getRealPackageName(packageName);
                Utils.Companion.append2TextPane("View running services related with " + name + " : \n", JBColor.BLUE, tp);
                AdbFacade.INSTANCE.getActivityService(mProject, name, s -> {
                    Utils.Companion.append2TextPane(s, tp);
                    return null;
                });
            }
        });

        mForegroundActivityButton.addActionListener(e -> {
            Utils.Companion.append2TextPane("Foreground Activity : \n", JBColor.BLUE, tp);
            AdbFacade.INSTANCE.showForegroundActivity(mProject, s -> {
                if (Utils.Companion.isEmpty(s)) {
                    Utils.Companion.append2TextPaneNewLine("get foreground Activity failure", JBColor.RED, tp);
                } else {
                    Utils.Companion.append2TextPaneNewLine(s, tp);
                }
                return null;
            });
        });
        mMonkeyTestButton.addActionListener(e -> {
            List<String> selectedValuesList = mJList.getSelectedValuesList();
            String name = "";
            if (!selectedValuesList.isEmpty()) {
                name = getRealPackageName(selectedValuesList.get(0));
                Utils.Companion.append2TextPane("Monkey test of " + name + " :\n", JBColor.BLUE, tp);
            }
            String countStr = JOptionPane.showInputDialog("Enter test count(only integers):");
            if (countStr == null || countStr.isEmpty()) {
                HelperMethodsKt.showErrorMsg("count can not empty");
                return;
            }
            int count = 0;
            try {
                count = Integer.parseInt(countStr);
            } catch (NumberFormatException e1) {
                HelperMethodsKt.showErrorMsg("parse count error,You can only enter integers");
                return;
            }
            if (count > 0) {
                AdbFacade.INSTANCE.monkeyTest(mProject, name, count, s -> {
                    Utils.Companion.append2TextPane(s, tp);
                    return null;
                });
            }
        });
        btn_search.addActionListener(e -> {
            Document document = tv_search.getDocument();
            try {
                String text = document.getText(0, document.getLength());
                if (!Utils.Companion.isEmpty(text)) {
                    Utils.Companion.searchAndSelection(text, tp);
                }
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        });

        setContentPane($$$getRootComponent$$$());
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

    public static void main(String... args) {
        ApplicationManagementFrame frame = new ApplicationManagementFrame(null);
        frame.pack();
        frame.setVisible(true);
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
            "center:max(d;4px):noGrow,center:44px:noGrow,center:32px:noGrow,center:d:noGrow,center:p:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        mPanel.setMinimumSize(new Dimension(590, 280));
        mPanel.setName("");
        mPanel.setPreferredSize(new Dimension(800, 620));
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
        panel3.setLayout(new FormLayout("fill:170px:grow,fill:d:grow,fill:153px:grow,fill:d:grow,fill:d:grow,fill:d:grow", "center:30px:grow,center:max(d;4px):noGrow"));
        mPanel.add(panel3, cc.xyw(1, 5, 4));
        mForegroundActivityButton = new JButton();
        mForegroundActivityButton.setText("Foreground Activity");
        panel3.add(mForegroundActivityButton, cc.xy(1, 2));
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
        sp_tp.setPreferredSize(new Dimension(590, 200));
        sp_tp.setVisible(true);
        sp_tp.setWheelScrollingEnabled(true);
        mPanel.add(sp_tp, cc.xyw(1, 6, 4, CellConstraints.FILL, CellConstraints.FILL));
        tp = new JTextPane();
        tp.setPreferredSize(new Dimension(590, 200));
        tp.setVisible(true);
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
