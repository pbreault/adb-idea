package com.developerphil.adbidea.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
/**
 * @describe
 * @author  Void Young
 * @date 2018-10-13 16:48:56
 */
public class RecordOptionDialog extends JDialog {
    private JPanel                          contentPane;
    private JButton                         buttonOK;
    private JButton                         buttonCancel;
    private JButton                         mStartButton;
    private JTextPane clickStartAndTheTextPane;
    private JCheckBox                       mDeleteCheckBox;
    @NotNull
    public  Function1<Boolean,Unit> okListener;
    @NotNull
    public  Function1<Unit,Unit> onStartListener;

    public RecordOptionDialog(@NotNull Function1<Boolean,Unit> okListener) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.okListener = okListener;
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - contentPane.getPreferredSize().width / 2;
        int y = (int) screensize.getHeight() / 2 - contentPane.getPreferredSize().height / 2;
        setTitle("Adb record option");
        setLocation(x, y);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        mStartButton.addActionListener(e -> onStartListener.invoke(null));

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        okListener.invoke(mDeleteCheckBox.isSelected());
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
