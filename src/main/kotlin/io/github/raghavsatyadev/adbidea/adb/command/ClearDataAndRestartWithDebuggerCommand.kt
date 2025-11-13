package io.github.raghavsatyadev.adbidea.adb.command

class ClearDataAndRestartWithDebuggerCommand :
    CommandList(ClearDataCommand(), StartDefaultActivityCommand(true))
