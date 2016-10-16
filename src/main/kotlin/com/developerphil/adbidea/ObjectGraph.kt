package com.developerphil.adbidea

import com.developerphil.adbidea.dagger.DaggerPluginComponent
import com.developerphil.adbidea.dagger.PluginComponent
import com.developerphil.adbidea.dagger.PluginModule
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project

class ObjectGraph(project: Project) : ProjectComponent,
        PluginComponent by DaggerPluginComponent.builder().pluginModule(PluginModule(project)).build() {

    override fun projectOpened() {
    }

    override fun projectClosed() {
    }

    override fun initComponent() {
    }

    override fun disposeComponent() {
    }

    override fun getComponentName(): String {
        return "DaggerObjectGraph"
    }
}
