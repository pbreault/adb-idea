package com.developerphil.adbidea.ui;

import com.developerphil.adbidea.ConstKt;
import com.developerphil.adbidea.HelperMethodsKt;
import com.developerphil.adbidea.adb.AdbFacade;
import com.developerphil.adbidea.bean.BoundItemBean;
import com.intellij.openapi.project.Project;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.jetbrains.annotations.Nullable;

/**
 * Created by XQ Yang on 10/10/2018  11:25 AM.
 * Description :
 */

public class InteractingFrame extends JFrame {

    private final Project         mProject;
    private       JPanel          mPanel;
    private       JRadioButton    mStartActivityRadioButton;
    private       JRadioButton    mStartServiceRadioButton;
    private       JRadioButton    mSendBroadcastRadioButton;
    private       JComboBox       mCbAction;
    private       JComboBox       mCbCategory;
    private       JComboBox       mCbComponent;
    private       BoundTypeJTable mTable;
    private       JButton         mGoButton;
    private       JButton         mAddRowButton;
    private       JButton         mDeleteRowButton;
    private       JButton         mClearButton;
    private       BoundTableModel mModel;

    public InteractingFrame(@Nullable Project project) {
        setResizable(true);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - mPanel.getPreferredSize().width / 2;
        int y = (int) screensize.getHeight() / 2 - mPanel.getPreferredSize().height / 2;
        setTitle("Adb Interacting with applications");
        URL filename = getClass().getResource("/icon.png");
        ImageIcon icon = new ImageIcon(filename);
        setIconImage(icon.getImage());
        setLocation(x, y);
        mProject = project;

        mCbAction.setModel(new DefaultComboBoxModel(ConstKt.getStartActivityActions()));
        mCbCategory.setModel(new DefaultComboBoxModel(ConstKt.getCategorys()));

        mStartActivityRadioButton.addActionListener(e -> mCbAction.setModel(new DefaultComboBoxModel(ConstKt.getStartActivityActions())));
        mSendBroadcastRadioButton.addActionListener(e -> mCbAction.setModel(new DefaultComboBoxModel(ConstKt.getBroadCastActions())));

        boundInit();
        mGoButton.addActionListener(e -> {
            Object selectedItem = mCbComponent.getSelectedItem();
            String name = selectedItem != null ? selectedItem.toString() : "";
            if (!mSendBroadcastRadioButton.isSelected() && Utils.isEmpty(name)) {
                HelperMethodsKt.showErrorMsg("Component name cannot be empty");
                return;
            }
            selectedItem = mCbAction.getSelectedItem();
            String action = selectedItem != null ? selectedItem.toString() : "";
            selectedItem = mCbCategory.getSelectedItem();
            String category = selectedItem != null ? selectedItem.toString() : "";
            List<BoundItemBean> boundData = mModel.getData();
            int type = mStartActivityRadioButton.isSelected() ? 0 : (mStartServiceRadioButton.isSelected() ? 1 : 2);
            AdbFacade.interacting(mProject, type, action, category, name, boundData);
        });

        setContentPane(mPanel);
    }

    public void boundInit() {
        mModel = new BoundTableModel();
        mTable.setModel(mModel);
        mTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        mTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        mTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        mTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        mTable.setRowHeight(25);
        mTable.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    mModel.removeRow(mTable.getSelectedRow());
                }
            }
        });
        mAddRowButton.addActionListener(e -> {
            mModel.addEmptyRow();
            mTable.requestFocus();
            int index = mModel.getRowCount() - 1;
            mTable.setRowSelectionInterval(index, index);
            mTable.editCellAt(index, 1);
        });
        mDeleteRowButton.addActionListener(e -> mModel.removeRow(mTable.getSelectedRow()));
        mClearButton.addActionListener(e -> mModel.setData(new ArrayList<>()));
    }

    private void createUIComponents() {
        mTable = new BoundTypeJTable();
    }
}
