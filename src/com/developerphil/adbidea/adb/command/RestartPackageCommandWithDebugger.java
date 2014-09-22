package com.developerphil.adbidea.adb.command;

public class RestartPackageCommandWithDebugger extends CommandList {

    public RestartPackageCommandWithDebugger() {
        super(new KillCommand(), new StartDefaultActivityCommandWithDebugger());
    }
}
