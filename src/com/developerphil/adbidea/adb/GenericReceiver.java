package com.developerphil.adbidea.adb;

import com.android.ddmlib.MultiLineReceiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Created by pbreault on 10/6/13.
*/
public final class GenericReceiver extends MultiLineReceiver {

    private static final String SUCCESS_OUTPUT = "Success"; //$NON-NLS-1$
    private static final Pattern FAILURE_PATTERN = Pattern.compile("Failure\\s+\\[(.*)\\]"); //$NON-NLS-1$

    private String mErrorMessage = null;

    public GenericReceiver() {
    }

    @Override
    public void processNewLines(String[] lines) {
        for (String line : lines) {
            if (!line.isEmpty()) {
                if (line.startsWith(SUCCESS_OUTPUT)) {
                    mErrorMessage = null;
                } else {
                    Matcher m = FAILURE_PATTERN.matcher(line);
                    if (m.matches()) {
                        mErrorMessage = m.group(1);
                    } else {
                        mErrorMessage = "Unknown failure";
                    }
                }
            }
        }
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
