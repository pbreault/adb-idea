package com.developerphil.adbidea.adb;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ConsoleUtil {

    static public Pair<Integer, ArrayList<String>> exec(String ...cmd) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(cmd);
        try {

            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            ArrayList<String> lines = new ArrayList<>();
            do {
                String line = reader.readLine();
                if (line == null)
                    break;
                lines.add(line);
            } while (true);

            int exitCode = process.waitFor();
            return new Pair<Integer, ArrayList<String>>(exitCode, lines);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
