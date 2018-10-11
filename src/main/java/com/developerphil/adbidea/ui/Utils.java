package com.developerphil.adbidea.ui;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Created by XQ Yang on 2018/6/25  18:14.
 * Description :
 */

public class Utils {
    public Utils() {
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }


    public static synchronized void append2TextPane(String str, Color color, JTextPane textPane) {
        Document doc = textPane.getDocument();
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

    public static void append2TextPane(String str, JTextPane textPane) {
        append2TextPane(str, null, textPane);
    }

    public static void append2TextPaneNewLine(String str, Color color, JTextPane textPane) {
        append2TextPane(str+"\n", color, textPane);
    }
    public static void append2TextPaneNewLine(String str, JTextPane textPane) {
        append2TextPane(str+"\n", null, textPane);
    }
}
