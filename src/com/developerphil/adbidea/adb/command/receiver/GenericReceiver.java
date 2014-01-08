package com.developerphil.adbidea.adb.command.receiver;

import com.android.ddmlib.MultiLineReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericReceiver extends MultiLineReceiver {

    private static final String SUCCESS_OUTPUT = "Success"; //$NON-NLS-1$
    private static final Pattern FAILURE_PATTERN = Pattern.compile("Failure\\s+\\[(.*)\\]"); //$NON-NLS-1$

    private String mErrorMessage = null;

    private List<String> adbOutputLines = new ArrayList<String>();

    public GenericReceiver() {
    }

    @Override
    public void processNewLines(String[] lines) {
        this.adbOutputLines.addAll(Arrays.asList(lines));

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

    public List<String> getAdbOutputLines() {
        return adbOutputLines;
    }

    public boolean isSuccess() {
        return mErrorMessage == null;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
