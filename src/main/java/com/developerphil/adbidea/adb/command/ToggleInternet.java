package com.developerphil.adbidea.adb.command;

/**
 * Created by fmatos on 1/05/2016.
 */
public class ToggleInternet extends ShellCommand {

    private final boolean turnOn;


    public ToggleInternet(boolean turnOn) {
        this.turnOn = turnOn;
    }

    @Override
    public String getCommandLine() {

        return "svc data " + (turnOn ? "enable" : "disable");
    }
}
