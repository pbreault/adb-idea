package com.developerphil.adbidea.adb.command

class ClearDataAndRestartCommand : CommandList(ClearDataCommand(), StartDefaultActivityCommand(false))