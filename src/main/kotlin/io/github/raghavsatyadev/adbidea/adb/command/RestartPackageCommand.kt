package io.github.raghavsatyadev.adbidea.adb.command

class RestartPackageCommand : CommandList(KillCommand(), StartDefaultActivityCommand(false))
