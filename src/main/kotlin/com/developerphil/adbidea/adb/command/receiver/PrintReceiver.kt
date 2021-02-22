package com.developerphil.adbidea.adb.command.receiver

import com.android.ddmlib.IShellOutputReceiver
import com.developerphil.adbidea.ui.Utils.Companion.isEmpty
import com.google.common.base.Charsets

class PrintReceiver : IShellOutputReceiver {
    private var mString: String? = null
    override fun addOutput(data: ByteArray, offset: Int, length: Int) {
        if (!this.isCancelled) {
            mString = String(data, offset, length, Charsets.UTF_8) + "\r\n"
        }
    }

    override fun flush() {}
    override fun isCancelled(): Boolean {
        return false
    }

    override fun toString(): String {
        return if (isEmpty(mString)) "" else mString!!
    }
}