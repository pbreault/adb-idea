package com.draekko.adbtools.adb.command;

public class ClearDataAndRestartCommandWithDebugger extends CommandList {
    public ClearDataAndRestartCommandWithDebugger() {
        super(new ClearDataCommand(), new StartDefaultActivityCommandWithDebugger());
    }
}
