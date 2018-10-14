package com.developerphil.adbidea.adb.command.receiver;

import com.android.ddmlib.IShellOutputReceiver;
import com.developerphil.adbidea.ui.Utils;
import com.google.common.base.Charsets;

public class PrintReceiver implements IShellOutputReceiver {

    private String mString;

    public final void addOutput(byte[] data, int offset, int length) {
        if (!this.isCancelled()) {
            mString = new String(data, offset, length, Charsets.UTF_8) + "\r\n";
        }
    }

    @Override
    public void flush() {

    }

    public void done() {
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public String toString() {
        return Utils.isEmpty(mString) ? "" : mString;
    }
}
