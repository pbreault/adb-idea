package com.developerphil.adbidea.ui;

/**
 * Created by XQ Yang on 2018/6/25  18:14.
 * Description :
 */

public class Utils {
    public Utils() {
    }

    public static boolean isEmpty(CharSequence s) {
        if (s == null) {
            return true;
        } else {
            return s.length() == 0;
        }
    }
}
