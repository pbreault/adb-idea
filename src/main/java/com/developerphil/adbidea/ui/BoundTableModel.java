package com.developerphil.adbidea.ui;

import com.developerphil.adbidea.bean.BoundDataType;
import com.developerphil.adbidea.bean.BoundItemBean;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.StringUtils;

public class BoundTableModel extends AbstractTableModel{
    private Class[] cellType = { Boolean.class, String.class, String.class, BoundDataType.class };
    private String title[] = { "select", "key", "value" ,"type"};

    private List<BoundItemBean> data = new ArrayList<>();

    public BoundTableModel() {
    }


    public List<BoundItemBean> getData() {
        return data.stream().filter(BoundItemBean -> BoundItemBean.getSelected()&&StringUtils.isNotEmpty(BoundItemBean.getKey())).collect(Collectors.toList());
    }

    public void setData(List<BoundItemBean> data) {
        this.data.clear();
        this.data.addAll(data);
        fireTableDataChanged();
    }

    public void clear() {
        int size = this.data.size();
        this.data.clear();
        fireTableRowsDeleted(0, size);
    }

    public void addEmptyRow() {
        data.add(new BoundItemBean(true, "", "",BoundDataType.NULL));
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void removeRow(int row) {
        if (row > -1 && row < data.size()) {
            data.remove(row);
        }
        fireTableRowsDeleted(row, row);
    }

    @Override
    public Class<?> getColumnClass(int arg0) {
        return cellType[arg0];
    }

    @Override
    public String getColumnName(int arg0) {
        return title[arg0];
    }

    @Override
    public int getColumnCount() {
        return title.length;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < data.size()) {
            switch (columnIndex) {
                case 0:
                    return data.get(rowIndex).getSelected();
                case 1:
                    return data.get(rowIndex).getKey();
                case 2:
                    return data.get(rowIndex).getValue();
                case 3:
                    return data.get(rowIndex).getDataType();
            }
        }
        return null;
    }

    //重写isCellEditable方法

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    //重写setValueAt方法
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < data.size()) {
            switch (columnIndex) {
                case 0:
                    data.get(rowIndex).setSelected((Boolean) aValue);
                    break;
                case 1:
                    data.get(rowIndex).setKey((String) aValue);
                    break;
                case 2:
                    data.get(rowIndex).setValue((String) aValue);
                    break;
                case 3:
                    data.get(rowIndex).setDataType((BoundDataType) aValue);
                    break;
            }
        }
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }


}
