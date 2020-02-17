package com.developerphil.adbidea.ui

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog
import org.jetbrains.android.facet.AndroidFacet

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
            previousSelectedModule?.let { selectElements(listOf(it)) }
            show()
            return if (chosenElements.isEmpty()) {
                null
            } else {
                chosenElements[0]
            }
        }
    }

    private fun saveModuleName(project: Project, moduleName: String) {
        PropertiesComponent.getInstance(project).setValue(SELECTED_MODULE_PROPERTY, moduleName)
    }

    private fun getSavedModuleName(project: Project): String? {
        return PropertiesComponent.getInstance(project).getValue(SELECTED_MODULE_PROPERTY)
    }
}

private val SELECTED_MODULE_PROPERTY = ModuleChooserDialogHelper::class.java.canonicalName + "-SELECTED_MODULE"