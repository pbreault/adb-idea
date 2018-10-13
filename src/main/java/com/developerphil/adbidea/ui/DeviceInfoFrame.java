package com.developerphil.adbidea.ui;

import com.developerphil.adbidea.adb.AdbFacade;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.MouseInputAdapter;
import org.jetbrains.annotations.Nullable;

/**
 * Created by XQ Yang on 10/11/2018  11:45 AM.
 * Description : show some device information
 */

public class DeviceInfoFrame extends JFrame {
    private final Project    mProject;
    private final JPopupMenu mPopupMenu;
    private       JPanel     mPanel;
    private       JTextPane  mTextPane;
    private       JButton    mDisplaysInfoButton;
    private       JButton    mCPUInfoButton;
    private       JButton    mMemoryInfoButton;
    private JButton          mScreenInfoButton;
    private JButton          mBatteryInfoButton;
    private JButton          mSystemButton;
    private JButton          mNetworkButton;
    private JButton          mMoreButton;

    private String androidVersion = "";

    public DeviceInfoFrame(@Nullable Project project) {
        setResizable(true);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - mPanel.getPreferredSize().width / 2;
        int y = (int) screensize.getHeight() / 2 - mPanel.getPreferredSize().height / 2;
        setTitle("Adb Interacting with applications");
        setLocation(x, y);
        URL filename = getClass().getResource("/icon.png");
        ImageIcon icon = new ImageIcon(filename);
        setIconImage(icon.getImage());
        mProject = project;
        mPopupMenu = new JPopupMenu();
        JMenuItem clear = mPopupMenu.add(new JMenuItem("clear"));
        clear.addActionListener(e -> mTextPane.setText(""));
        mTextPane.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    mPopupMenu.show(mTextPane, e.getX(), e.getY());
                }
            }
        });
        //how to switch thread?
        AdbFacade.getSimpleInfo(mProject, "getprop ro.product.brand", "get Device brand ", s -> {
            Utils.append2TextPaneNewLine("Device brand:", JBColor.BLUE, mTextPane);
            Utils.append2TextPane(s, mTextPane);
            return null;
        });
        AdbFacade.getSimpleInfo(mProject, "getprop ro.product.model", "get Device model ", s1 -> {
            Utils.append2TextPaneNewLine("Device model:", JBColor.BLUE, mTextPane);
            Utils.append2TextPane(s1, mTextPane);
            return null;
        });
        AdbFacade.getSimpleInfo(mProject, "getprop ro.product.name", "get Device name ", s2 -> {
            Utils.append2TextPaneNewLine("Device name:", JBColor.BLUE, mTextPane);
            Utils.append2TextPaneNewLine(s2, mTextPane);
            return null;
        });
        AdbFacade.getSimpleInfo(mProject, "getprop ro.build.version.release", "get Android Version ", s1 -> {
            androidVersion = s1.trim();
            return null;
        });
        mDisplaysInfoButton.addActionListener(e -> getInfo2Show("Displays Info:", "dumpsys window displays", "get Displays Info "));
        mCPUInfoButton.addActionListener(e -> {
            getInfo2Show("CPU Info:", "cat /proc/cpuinfo", "get CPU Info ");
            getInfo2Show("Support ABI list:", "getprop ro.product.cpu.abilist", "Support ABI list ");
        });
        mMemoryInfoButton.addActionListener(e -> {
            getInfo2Show("Memory Info:", "cat /proc/meminfo", "get Memory Info ");
            getInfo2Show("Support heap size:", "getprop dalvik.vm.heapsize", "get Support heap ");
        });
        mScreenInfoButton.addActionListener(e -> {
            getInfo2Show("Screen Size:", "wm size", "get Screen Size ");
            getInfo2Show("Screen density:", "wm density", "get Screen density ");
        });
        mBatteryInfoButton.addActionListener(e -> {
            getInfo2Show("Battery Service state:", "dumpsys battery", "get Battery Service state ");
        });
        mMoreButton.addActionListener(e -> {
            getInfo2Show("More system and hardware information:", "cat /system/build.prop", "get System and hardware information ");
        });
        mSystemButton.addActionListener(e -> {
            getInfo2Show("Android id:", "settings get secure android_id", "get Android id ");
            AdbFacade.getSimpleInfo(mProject, "getprop ro.build.version.sdk", "get Android sdk ", sdk -> {
                Utils.append2TextPaneNewLine("Android SDK:", JBColor.BLUE, mTextPane);
                Utils.append2TextPane(sdk, mTextPane);
                return null;
            });
            if (!Utils.isEmpty(androidVersion)) {
                Utils.append2TextPaneNewLine("Android Version:", JBColor.BLUE, mTextPane);
                Utils.append2TextPaneNewLine(androidVersion, mTextPane);
            }

        });
        mNetworkButton.addActionListener(e -> {
            getInfo2Show("Android id:", "settings get secure android_id", "get Android id ");
            if (!Utils.isEmpty(androidVersion)) {
                try {
                    int verison = Integer.parseInt(androidVersion.substring(0, 1));
                    if (verison >= 5) {
                        //cannot work
                        //getInfo2Show("IMEI 1 :", "service call iphonesubinfo 1 | awk -F \"'\" '{print $2}' | sed '1 d' | tr -d '.' | awk '{print}' ORS=", "get IMEI 1 ");
                        Utils.append2TextPaneNewLine("Can not get IMEI", JBColor.red, mTextPane);
                    } else {
                        getInfo2Show("IMEI 1 :", "dumpsys iphonesubinfo", "get IMEI 1 ");
                    }
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                    Utils.append2TextPaneNewLine("Can not get IMEI", JBColor.red, mTextPane);
                }
            }
            AdbFacade.getSimpleInfo(mProject, "ifconfig | grep Mask", "get IP Address ", s -> {
                Utils.append2TextPaneNewLine("ifconfig :", JBColor.BLUE, mTextPane);
                Utils.append2TextPaneNewLine(s, mTextPane);
                return null;
            });
            AdbFacade.getSimpleInfo(mProject, "ifconfig wlan0", "get IP Address ", s1 -> {
                Utils.append2TextPaneNewLine("wlan0 :", JBColor.BLUE, mTextPane);
                Utils.append2TextPaneNewLine(s1, mTextPane);
                return null;
            });
            getInfo2Show("Mac Address:", "cat /sys/class/net/wlan0/address", "get Mac Address ");
        });

        setContentPane(mPanel);
    }

    public void getInfo2Show(String item, String command, String desc) {
        AdbFacade.getSimpleInfo(mProject, command, desc, s -> {
            Utils.append2TextPaneNewLine(item, JBColor.BLUE, mTextPane);
            Utils.append2TextPaneNewLine(s, mTextPane);
            return null;
        });
    }
}
