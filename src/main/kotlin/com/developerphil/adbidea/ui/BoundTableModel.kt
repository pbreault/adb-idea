package com.developerphil.adbidea.ui

import com.developerphil.adbidea.bean.BoundDataType
import com.developerphil.adbidea.bean.BoundItemBean
import java.util.*
import javax.swing.table.AbstractTableModel

class BoundTableModel : AbstractTableModel() {
    private val cellType = arrayOf<Class<*>>(Boolean::class.java, String::class.java, String::class.java, BoundDataType::class.java)
    private val title = arrayOf("select", "key", "value", "type")

    private val data = ArrayList<BoundItemBean>()


    fun getData(): List<BoundItemBean> {
        return data.filter { it.selected&& it.key.isNotEmpty()}
    }

    fun setData(data: List<BoundItemBean>) {
        this.data.clear()
        this.data.addAll(data)
        fireTableDataChanged()
    }

    fun clear() {
        val size = this.data.size
        this.data.clear()
        fireTableRowsDeleted(0, size)
    }

    fun addEmptyRow() {
        data.add(BoundItemBean(true, "", "", BoundDataType.STRING))
        fireTableRowsInserted(data.size - 1, data.size - 1)
    }

    fun removeRow(row: Int) {
        if (row > -1 && row < data.size) {
            data.removeAt(row)
        }
        fireTableRowsDeleted(row, row)
    }

    override fun getColumnClass(arg0: Int): Class<*> {
        return cellType[arg0]
    }

    override fun getColumnName(arg0: Int): String {
        return title[arg0]
    }

    override fun getColumnCount(): Int {
        return title.size
    }

    override fun getRowCount(): Int {
        return data.size
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        if (rowIndex < data.size) {
            when (columnIndex) {
                0 -> return data[rowIndex].selected
                1 -> return data[rowIndex].key
                2 -> return data[rowIndex].value
                3 -> return data[rowIndex].dataType
            }
        }
        return null
    }

    //重写isCellEditable方法

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return true
    }

    //重写setValueAt方法
    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        if (rowIndex < data.size) {
            when (columnIndex) {
                0 -> data[rowIndex].selected = aValue as Boolean
                1 -> data[rowIndex].key = aValue as String
                2 -> data[rowIndex].value = aValue as String
                3 -> data[rowIndex].dataType = aValue as BoundDataType
            }
        }
        this.fireTableCellUpdated(rowIndex, columnIndex)
    }


}
