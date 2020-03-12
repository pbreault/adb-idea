package com.developerphil.adbidea.ui

import javax.swing.JTable
import javax.swing.table.TableCellEditor

/**
 * Created by XQ Yang on 10/10/2018  2:08 PM.
 * Description :
 */

class BoundTypeJTable : JTable() {

    private val mBoundTypeCellEditor = BoundTypeCellEditor()

    override fun getCellEditor(row: Int, column: Int): TableCellEditor {
        return if (column == 3) {
            mBoundTypeCellEditor
        } else super.getCellEditor(row, column)
    }
}

