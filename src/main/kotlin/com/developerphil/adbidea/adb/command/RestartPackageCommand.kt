package com.developerphil.adbidea.adb.command

class RestartPackageCommand : CommandList(KillCommand(), StartDefaultActivityCommand(false))