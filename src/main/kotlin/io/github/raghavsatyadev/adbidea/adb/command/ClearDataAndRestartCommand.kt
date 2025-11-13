package io.github.raghavsatyadev.adbidea.adb.command

class ClearDataAndRestartCommand :
    CommandList(ClearDataCommand(), StartDefaultActivityCommand(false))
