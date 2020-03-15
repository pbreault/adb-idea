package com.developerphil.adbidea.ui

import javax.swing.AbstractListModel

/**
 * Created by XQ Yang on 10/8/2018  5:41 PM.
 * Description :
 */

internal class MyApplistModel(private val mList: MutableList<String>) : AbstractListModel<String>() {

    override fun getSize(): Int {
        return mList.size
    }

    override fun getElementAt(index: Int): String {
        return mList[index]
    }

    fun delete(s: String) {
        val index = mList.indexOf(s)
        if (index != -1) {
            mList.removeAt(index)
            fireIntervalRemoved(this, index, index)
        }
    }
}
