package com.developerphil.adbidea.adb

import com.developerphil.adbidea.ObjectGraph
import com.developerphil.adbidea.adb.command.*
import com.developerphil.adbidea.ui.NotificationHelper
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.intellij.openapi.project.Project
import java.util.concurrent.Executors

object AdbFacade {
    private val EXECUTOR = Executors.newCachedThreadPool(ThreadFactoryBuilder().setNameFormat("AdbIdea-%d").build())

    fun uninstall(project: Project) = executeOnDevice(project, UninstallCommand())
    fun kill(project: Project) = executeOnDevice(project, KillCommand())
    fun grantPermissions(project: Project) = executeOnDevice(project, GrantPermissionsCommand())
    fun revokePermissions(project: Project) = executeOnDevice(project, RevokePermissionsCommand())
    fun revokePermissionsAndRestart(project: Project) = executeOnDevice(project, RevokePermissionsAndRestartCommand())
    fun startDefaultActivity(project: Project) = executeOnDevice(project, StartDefaultActivityCommand(false))
    fun startDefaultActivityWithDebugger(project: Project) = executeOnDevice(project, StartDefaultActivityCommand(true))
    fun restartDefaultActivity(project: Project) = executeOnDevice(project, RestartPackageCommand())
    fun restartDefaultActivityWithDebugger(project: Project) = executeOnDevice(project, CommandList(KillCommand(), StartDefaultActivityCommand(true)))
    fun clearData(project: Project) = executeOnDevice(project, ClearDataCommand())
    fun clearDataAndRestart(project: Project) = executeOnDevice(project, ClearDataAndRestartCommand())

    private fun executeOnDevice(project: Project, runnable: Command) {
        if (AdbUtil.isGradleSyncInProgress(project)) {
            NotificationHelper.error("Gradle sync is in progress")
            return
        }

        val result = project.getComponent(ObjectGraph::class.java)
                .deviceResultFetcher
                .fetch()

        if (result != null) {
            for (device in result.devices) {
                EXECUTOR.submit { runnable.run(project, device, result.facet, result.packageName) }
            }
        } else {
            NotificationHelper.error("No Device found")
        }
    }
}