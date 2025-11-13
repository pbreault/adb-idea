package io.github.raghavsatyadev.adbidea.adb.command

class RevokePermissionsAndRestartCommand :
    CommandList(RevokePermissionsCommand(), StartDefaultActivityCommand(false))
