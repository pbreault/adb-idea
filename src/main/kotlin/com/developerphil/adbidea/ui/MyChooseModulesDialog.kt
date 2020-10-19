package com.developerphil.adbidea.ui

import com.intellij.icons.AllIcons
import com.intellij.ide.util.ChooseElementsDialog
import com.intellij.openapi.project.Project
import javax.swing.Icon

class MyChooseModulesDialog(project: Project, items: List<String>, title: String, description: String, private val mIcon: Icon) : ChooseElementsDialog<String>(project, items,
    title, description, false) {

    fun setSingleSelectionMode() {
        this.myChooser.setSingleSelectionMode()
    }

    override fun getItemText(s: String): String {
        return s
    }

    override fun getItemIcon(s: String): Icon? {
        return if (s == ModuleChooserDialogHelper.DO_NOT_SELECT_THE_DEFAULT_MODULE) AllIcons.Actions.Close else mIcon
    }
}