package com.developerphil.adbidea.adb.command

class ClearDataAndRestartWithDebuggerCommand : CommandList(ClearDataCommand(), StartDefaultActivityCommand(true))
