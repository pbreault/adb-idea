package com.draekko.adbtools.adb.command;

public class RestartPackageCommandWithDebugger extends CommandList {

    public RestartPackageCommandWithDebugger() {
        super(new KillCommand(), new StartDefaultActivityCommandWithDebugger());
    }
}
