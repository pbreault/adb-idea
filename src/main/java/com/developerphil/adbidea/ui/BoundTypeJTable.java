package com.developerphil.adbidea.ui;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Created by XQ Yang on 10/10/2018  2:08 PM.
 * Description :
 */

public class BoundTypeJTable extends JTable {

    private BoundTypeCellEditor mBoundTypeCellEditor= new BoundTypeCellEditor();

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == 3) {
            return mBoundTypeCellEditor;
        }
        return super.getCellEditor(row, column);
    }
}

