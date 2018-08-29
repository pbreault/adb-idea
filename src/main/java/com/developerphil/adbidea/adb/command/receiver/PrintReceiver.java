package com.developerphil.adbidea.adb.command.receiver;

import java.util.List;

public class PrintReceiver extends GenericReceiver {

    @Override
    public String toString() {
        List<String> outputLines = getAdbOutputLines();
        if (!outputLines.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            int tabCount = 0;
            for (String line : outputLines) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
                if (line.isEmpty()) {
                    tabCount = 0;
                    continue;
                }
                if (line.endsWith(":")) {
                    tabCount++;
                }
                for (int i = 0; i < tabCount; i++) {
                    stringBuilder.append("  ");
                }
            }
            return stringBuilder.toString();
        }
        return super.toString();
    }
}
