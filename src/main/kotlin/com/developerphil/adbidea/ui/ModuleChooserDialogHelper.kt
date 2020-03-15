package com.developerphil.adbidea.ui

import com.google.common.collect.Lists
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet


object ModuleChooserDialogHelper {

    val SELECTED_MODULE_PROPERTY = ModuleChooserDialogHelper::class.java.canonicalName + "-SELECTED_MODULE"
    val DEFAULT_MODULE_PROPERTY = ModuleChooserDialogHelper::class.java.canonicalName + "-DEFAULT_MODULE"

    val DO_NOT_SELECT_THE_DEFAULT_MODULE = "Do not select the default module"

    fun showDialogForFacets(project: Project, facets: List<AndroidFacet>, isSetDefault: Boolean): AndroidFacet? {
        val modules = Lists.newArrayList<Module>()
        val modulesName = Lists.newArrayList<String>()
        val previousModuleName = getPreviousModuleName(project)
        val defaultModuleName = getDefaultModuleName(project)
        var previousSelectedModule: List<String>? = null
        for (facet in facets) {
            val module = facet.module
            val name = module.name
            if (!isSetDefault && name == defaultModuleName) {
                return facet
            }
            modules.add(module)
            modulesName.add(name)
            if (name == previousModuleName) {
                previousSelectedModule = Lists.newArrayList(name)
            } else if (isSetDefault && name == defaultModuleName) {
                previousSelectedModule = Lists.newArrayList(name)
            }
        }

        if (isSetDefault && previousSelectedModule == null) {
            previousSelectedModule = Lists.newArrayList(DO_NOT_SELECT_THE_DEFAULT_MODULE)
        }

        if (isSetDefault) {
            modulesName.add(DO_NOT_SELECT_THE_DEFAULT_MODULE)
        }
        var titleSelectDefault = "Choose Default Module"
        if (!Utils.isEmpty(defaultModuleName)) {
            titleSelectDefault += ",Current module is :" + defaultModuleName!!
        }
        val dialog = MyChooseModulesDialog(project, modulesName, if (isSetDefault) titleSelectDefault else "Choose Module",
            if (isSetDefault) "Set the default module for each operation" else "", ModuleType.get(modules[0]).icon)
        dialog.setSingleSelectionMode()
        if (previousSelectedModule != null) {
            dialog.selectElements(previousSelectedModule)
        }
        dialog.show()

        val chosenElements = dialog.chosenElements
        if (chosenElements.isEmpty()) {
            return null
        }

        val chosenModule = chosenElements[0]
        if (isSetDefault) {
            if (chosenModule == DO_NOT_SELECT_THE_DEFAULT_MODULE) {
                saveDefaultModuleName(project, "")
                return null
            } else {
                saveDefaultModuleName(project, chosenModule)
            }
        } else {
            saveModuleName(project, chosenModule)
        }
        val chosenModuleIndex = modulesName.indexOf(chosenModule)
        return facets[chosenModuleIndex]
    }

    private fun saveDefaultModuleName(project: Project, moduleName: String) {
        val properties = PropertiesComponent.getInstance(project)
        properties.setValue(DEFAULT_MODULE_PROPERTY, moduleName)
    }

    private fun saveModuleName(project: Project, moduleName: String) {
        val properties = PropertiesComponent.getInstance(project)
        properties.setValue(SELECTED_MODULE_PROPERTY, moduleName)
    }

    private fun getPreviousModuleName(project: Project): String? {
        val properties = PropertiesComponent.getInstance(project)
        return properties.getValue(SELECTED_MODULE_PROPERTY)
    }

    private fun getDefaultModuleName(project: Project): String? {
        val properties = PropertiesComponent.getInstance(project)
        return properties.getValue(DEFAULT_MODULE_PROPERTY)
    }


}