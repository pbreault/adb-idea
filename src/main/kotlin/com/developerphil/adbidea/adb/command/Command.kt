package com.developerphil.adbidea.adb.command

import com.android.ddmlib.IDevice
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet

interface Command {
    /**
     * @return true if the command executed properly
     */
    fun run(project: Project, device: IDevice, facet: AndroidFacet, packageName: String): Boolean
}