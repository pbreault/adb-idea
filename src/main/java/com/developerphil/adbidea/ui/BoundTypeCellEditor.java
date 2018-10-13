package com.developerphil.adbidea.ui;

import com.developerphil.adbidea.bean.BoundDataType;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * Created by XQ Yang on 10/10/2018  2:12 PM.
 * Description :
 */

public class BoundTypeCellEditor extends DefaultCellEditor {

    public BoundTypeCellEditor() {
        super(new JComboBox<>(BoundDataType.values()));
    }
}
