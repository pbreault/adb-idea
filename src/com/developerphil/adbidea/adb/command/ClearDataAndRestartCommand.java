package com.developerphil.adbidea.adb.command;

/**
 * Created by pbreault on 1/2/14.
 */
public class ClearDataAndRestartCommand extends CommandList {
    public ClearDataAndRestartCommand() {
        super(new ClearDataCommand(), new StartDefaultActivityCommand());
    }
}
