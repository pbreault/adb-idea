package com.developerphil.adbidea.adb

import com.developerphil.adbidea.ObjectGraph
import com.developerphil.adbidea.adb.command.*
import com.developerphil.adbidea.bean.BoundItemBean
import com.developerphil.adbidea.ui.NotificationHelper
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.intellij.openapi.project.Project
import java.io.File
import java.util.concurrent.Executors

object AdbFacade {
    private val EXECUTOR = Executors.newCachedThreadPool(ThreadFactoryBuilder().setNameFormat("AdbIdea-%d").build())



    fun uninstall(project: Project, packageName: String) {
        executeOnDevice(project, UninstallCommand(packageName))
    }

    fun uninstall(project: Project) {
        executeOnDevice(project, UninstallCommand())
    }

    fun installApk(project: Project, apks: List<File>) {
        executeOnDevice(project, InstallApkCommand(apks))
    }

    fun kill(project: Project) {
        executeOnDevice(project, KillCommand())
    }

    fun grantPermissions(project: Project) {
        executeOnDevice(project, GrantPermissionsCommand())
    }

    fun revokePermissions(project: Project) {
        executeOnDevice(project, RevokePermissionsCommand())
    }

    fun revokePermissionsAndRestart(project: Project) {
        executeOnDevice(project, RevokePermissionsAndRestartCommand())
    }

    fun startDefaultActivity(project: Project) {
        executeOnDevice(project, StartDefaultActivityCommand(false))
    }

    fun startDefaultActivityWithDebugger(project: Project) {
        executeOnDevice(project, StartDefaultActivityCommand(true))
    }

    fun restartDefaultActivity(project: Project) {
        executeOnDevice(project, RestartPackageCommand())
    }

    fun restartDefaultActivityWithDebugger(project: Project) {
        executeOnDevice(project, CommandList(KillCommand(), StartDefaultActivityCommand(true)))
    }

    fun clearData(project: Project) {
        executeOnDevice(project, ClearDataCommand())
    }

    fun getPackageDetail(project: Project, packageName: String, callback: Function1<String, Unit>) {
        executeOnDevice(project, PackageDetailCommand(packageName, callback))
    }

    fun forceStop(project: Project, packageName: String) {
        executeOnDevice(project, ForceStopCommand(packageName))
    }

    fun getPackagePath(project: Project, packageName: String, callback: Function1<String, Unit>) {
        executeOnDevice(project, PackagePathCommand(packageName, callback))
    }

    fun getActivityService(project: Project, packageName: String, callback: Function1<String, Unit>) {
        executeOnDevice(project, ActivityServiceCommand(packageName, callback))
    }

    fun clearDataAndRestart(project: Project) {
        executeOnDevice(project, ClearDataAndRestartCommand())
    }

    fun getAllApplicationList(project: Project, parameter: String, callback: Function1<List<String>, Unit>) {
        executeOnDevice(project, GetApplicationListCommand(parameter, callback))
    }

    private fun executeOnDevice(project: Project?, runnable: Command) {

        if (AdbUtil.isGradleSyncInProgress(project!!)) {
            NotificationHelper.error("Gradle sync is in progress")
            return
        }

        val result = project!!.getComponent(ObjectGraph::class.java).deviceResultFetcher.fetch()

        if (result != null) {
            for (device in result.devices) {
                EXECUTOR.submit { runnable.run(project, device, result.facet, result.packageName) }
            }
        } else {
            NotificationHelper.error("No Device found")
        }
    }

    fun clearData(project: Project, realPackageName: String) {
        executeOnDevice(project, ClearDataCommand(realPackageName))
    }

    fun showForegroundActivity(project: Project, callback: Function1<String, Unit>) {
        executeOnDevice(project, ForegroundActivityCommand(callback))
    }

    fun monkeyTest(project: Project, packageName: String, count: Int, callback: Function1<String, Unit>) {
        executeOnDevice(project, MonkeyTestCommand(packageName, count, callback))
    }

    fun putStringToDevice(project: Project?, str: String) {
        executeOnDevice(project, PutStringToDeviceCommand(str))
    }

    fun interacting(project: Project, type: Int, action: String, category: String, name: String, boundData: MutableList<BoundItemBean>) {
        executeOnDevice(project, getInteractingCommand(type, action, category, name, boundData))
    }

    fun getSimpleInfo(project: Project?, command: String, desc: String, callback: Function1<String, Unit>) {
        executeOnDevice(project, CommonStringResultCommand(command, desc, callback))
    }

    fun captureScreen(project: Project?, localDir: File, fileName: String) {
        executeOnDevice(project, CaptureScreenCommand(localDir, fileName))
    }

    /**
     * "bad result"
     * @param project
     * @param localFile
     * @param videoName
     * @param length
     * @param showTouches
     */
    @Deprecated("")
    fun recordScreen(project: Project?, localFile: File, videoName: String, length: Int, showTouches: Boolean) {
        executeOnDevice(project, ScreenRecordCommand(localFile, videoName, length, showTouches))
    }

    fun pullFile(project: Project?, remotePath: String, localFile: File, deleteRemoteFile: Boolean) {
        executeOnDevice(project, PullFileCommand(remotePath, localFile, deleteRemoteFile))
    }

    fun getDeviceModel(project: Project?, function: Function1<String, Unit>) {
        getSimpleInfo(project, "getprop ro.product.model", "get Device model ") { s ->
            function.invoke(s.replace("\n", "").replace("\r", "").replace(" ", ""))
            null
        }
    }
}