package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet

open class CommandList(vararg commands: Command) : Command {

    private val commands = listOf(*commands)

    override fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean {
        for (command in commands) {
            if (!command.run(project, device, facet, packageName)) {
                return false
            }
        }
        return true
    }

}