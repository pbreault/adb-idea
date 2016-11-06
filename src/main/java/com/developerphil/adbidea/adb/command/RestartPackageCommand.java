package com.developerphil.adbidea.adb.command;

public class RestartPackageCommand extends CommandList {

    public RestartPackageCommand() {
        super(new KillCommand(), new StartDefaultActivityCommand(false));
    }
}
