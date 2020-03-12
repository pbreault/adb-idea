package com.developerphil.adbidea.adb.command

class RevokePermissionsAndRestartCommand : CommandList(RevokePermissionsCommand(), StartDefaultActivityCommand(false))