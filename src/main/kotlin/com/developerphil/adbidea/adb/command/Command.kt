package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.android.facet.AndroidFacet

interface Command {
    /**
     * @return true if the command executed properly
     */
    fun run(context: CommandContext): Boolean
}

data class CommandContext(
    val project: Project,
    val device: IDevice,
    val facet: AndroidFacet,
    val packageName: String,
    val coroutineScope: CoroutineScope
)

