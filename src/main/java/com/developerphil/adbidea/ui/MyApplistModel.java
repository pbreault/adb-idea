package com.developerphil.adbidea.ui;

import java.util.List;
import javax.swing.AbstractListModel;

/**
 * Created by XQ Yang on 10/8/2018  5:41 PM.
 * Description :
 */

class MyApplistModel extends AbstractListModel<String> {

    private List<String> mList;

    public MyApplistModel(List<String> list) {
        mList = list;
    }

    @Override
    public int getSize() {
        return mList.size();
    }

    @Override
    public String getElementAt(int index) {
        return mList.get(index);
    }

    public void delete(String s) {
        int index = mList.indexOf(s);
        if (index != -1) {
            mList.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }
}
