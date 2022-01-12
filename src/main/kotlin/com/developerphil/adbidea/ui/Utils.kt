package com.developerphil.adbidea.ui

import java.awt.Color
import javax.swing.JOptionPane
import javax.swing.JTextPane
import javax.swing.text.BadLocationException
import javax.swing.text.MutableAttributeSet
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants


/**
 * Created by XQ Yang on 2018/6/25  18:14.
 * Description :
 */

class Utils {
    companion object {

        fun isEmpty(str: CharSequence?): Boolean {
            return str == null || str.isEmpty()
        }


        @Synchronized
        fun append2TextPane(str: String, color: Color?, textPane: JTextPane) {
            val doc = textPane.document
            if (doc != null) {
                try {
                    var attr: MutableAttributeSet? = null
                    if (color != null) {
                        attr = SimpleAttributeSet()
                        StyleConstants.setForeground(attr, color)
                        StyleConstants.setBold(attr, true)
                    }
                    doc.insertString(doc.length, str, attr)
                } catch (e: BadLocationException) {
                }

            }
        }

        fun searchAndSelection(str: String,textPane: JTextPane){
            val doc = textPane.document
            val root = doc.getDefaultRootElement()
            var nowSeg = 0
            //对每一个段落进行搜索
            if (nowSeg < root.getElementCount()) {
                var seg = root.getElement(nowSeg)
                try {
                    var line: String = doc.getText(seg.getStartOffset(), seg.getEndOffset() - seg.getStartOffset())
                    var start = line.indexOf(str)
                    while (-1 == start) {
                        nowSeg++
                        if (nowSeg >= root.getElementCount()) {
                            //搜索完毕
                            return
                        }
                        seg = root.getElement(nowSeg)
                        line = doc.getText(seg.getStartOffset(), seg.getEndOffset() - seg.getStartOffset())
                        start = line.indexOf(str)
                    }
                    textPane.requestFocus()
                    textPane.select(seg.getStartOffset() + start,seg.getStartOffset() + start + str.length)
                } catch (e1: BadLocationException) {
                    e1.printStackTrace()
                }
            }
        }

        fun append2TextPane(str: String, textPane: JTextPane) {
            append2TextPane(str, null, textPane)
        }

        fun append2TextPaneNewLine(str: String, color: Color, textPane: JTextPane) {
            append2TextPane(str + "\n", color, textPane)
        }

        fun append2TextPaneNewLine(str: String, textPane: JTextPane) {
            append2TextPane(str + "\n", null, textPane)
        }
    }
}
