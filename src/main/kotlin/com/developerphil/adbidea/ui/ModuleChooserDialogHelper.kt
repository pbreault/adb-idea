package com.developerphil.adbidea.ui

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.UIUtil
import org.jetbrains.android.facet.AndroidFacet
import java.awt.Component
import java.awt.Dimension
import java.awt.geom.Dimension2D
import javax.swing.JTable

object ModuleChooserDialogHelper {

    fun showDialogForFacets(project: Project, facets: List<AndroidFacet>): AndroidFacet? {
        val modules = facets.map { it.module }
        val previousModuleName = getSavedModuleName(project)
        val previousSelectedModule = modules.firstOrNull { it.name == previousModuleName }

        val selectedModule = showDialog(project, modules, previousSelectedModule) ?: return null
        saveModuleName(project, selectedModule.name)
        return facets[modules.indexOf(selectedModule)]
    }

    private fun showDialog(project: Project, modules: List<Module>, previousSelectedModule: Module?): Module? {
        with(ChooseModulesDialog(project, modules, "Choose Module", "")) {
            setSingleSelectionMode()
            getSizeForTableContainer(preferredFocusedComponent)?.let {
                setSize(it.width, it.height)
            }
            previousSelectedModule?.let { selectElements(listOf(it)) }
            return showAndGetResult().firstOrNull()
        }
    }

    // Fix an issue where the modules dialog is not wide enough to display the whole module name.
    // This code is lifted from com.intellij.openapi.ui.impl.DialogWrapperPeerImpl.MyDialog.getSizeForTableContainer
    private fun getSizeForTableContainer(component: Component?): Dimension? {
        if (component == null) return null
        val tables = UIUtil.uiTraverser(component).filter(JTable::class.java)
        if (!tables.isNotEmpty) return null
        val size = component.preferredSize
        for (table in tables) {
            val tableSize = table.preferredSize
            size.width = size.width.coerceAtLeast(tableSize.width)
            size.height = size.height.coerceAtLeast(tableSize.height + size.height - table.parent.height)
        }
        size.width = 1000.coerceAtMost(600.coerceAtLeast(size.width))
        size.height = 800.coerceAtMost(size.height)
        return size
    }

    private fun saveModuleName(project: Project, moduleName: String) {
        PropertiesComponent.getInstance(project).setValue(SELECTED_MODULE_PROPERTY, moduleName)
    }

    private fun getSavedModuleName(project: Project): String? {
        return PropertiesComponent.getInstance(project).getValue(SELECTED_MODULE_PROPERTY)
    }
}

private val SELECTED_MODULE_PROPERTY = ModuleChooserDialogHelper::class.java.canonicalName + "-SELECTED_MODULE"