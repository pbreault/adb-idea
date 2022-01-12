package com.developerphil.adbidea

import com.developerphil.adbidea.ui.Utils.Companion.isEmpty
import com.developerphil.adbidea.ui.Utils.Companion.searchAndSelection
import javax.swing.JTextField
import javax.swing.JTextPane
import javax.swing.text.BadLocationException

/**
 * @describe
 * @author  longforus
 * @date 2022/1/12  17:01
 */
class SearchHelper(private val tv_search: JTextField,private val tp: JTextPane) {

    private var lastSearchContent: String? = null
    private var lastSearchSeg = 0
    private var lastSearchEnd = -1

    fun doSearch() {
        val document = tv_search.document
        try {
            val text = document.getText(0, document.length)
            if (!isEmpty(text)) {
                if (text != lastSearchContent) {
                    lastSearchSeg = 0
                    lastSearchEnd = -1
                }
                val (first, second) = searchAndSelection(text, tp, lastSearchSeg, lastSearchEnd)
                lastSearchSeg = first
                lastSearchEnd = second
                lastSearchContent = text
            } else {
                lastSearchContent = null
                lastSearchEnd = -1
                lastSearchSeg = 0
            }
        } catch (badLocationException: BadLocationException) {
            badLocationException.printStackTrace()
        }
    }
}