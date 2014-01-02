package com.developerphil.adbidea.adb.command;

/**
 * Created by pbreault on 1/2/14.
 */
public class RestartPackageCommand extends CommandList {

    public RestartPackageCommand() {
        super(new KillCommand(), new StartDefaultActivityCommand());
    }
}
