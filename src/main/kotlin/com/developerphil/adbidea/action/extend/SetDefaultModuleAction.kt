package com.developerphil.adbidea.action.extend

import com.developerphil.adbidea.action.AdbAction
import com.developerphil.adbidea.adb.DeviceResultFetcher
import com.developerphil.adbidea.ui.ModuleChooserDialogHelper
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

/**
 * @describe
 * @author  longforus
 * @date 10/29/2018  4:21 PM
 */
class SetDefaultModuleAction : AdbAction() {

    override fun actionPerformed(e: AnActionEvent?, project: Project?) {
        project?.let {
            val facets = DeviceResultFetcher.getApplicationFacets(it)
            ModuleChooserDialogHelper.showDialogForFacets(project, facets, true)
        }
    }
}