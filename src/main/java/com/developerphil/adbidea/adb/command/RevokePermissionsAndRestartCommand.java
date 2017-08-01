package com.developerphil.adbidea.adb.command;

public class RevokePermissionsAndRestartCommand extends CommandList {
    public RevokePermissionsAndRestartCommand() {
        super(new RevokePermissionsCommand(), new StartDefaultActivityCommand(false));
    }
}
