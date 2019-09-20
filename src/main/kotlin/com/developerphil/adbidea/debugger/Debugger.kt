package com.developerphil.adbidea.debugger

import com.android.ddmlib.Client
import com.android.ddmlib.IDevice
import com.android.tools.idea.run.AndroidProcessHandler
import com.android.tools.idea.run.editor.AndroidDebugger
import com.developerphil.adbidea.invokeLater
import com.developerphil.adbidea.waitUntil
import com.intellij.execution.ExecutionManager
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.project.Project

class Debugger(private val project: Project, private val device: IDevice, private val packageName: String) {

    fun attach() {
        waitUntil { debuggerCanBeAttached() }
        for (androidDebugger in AndroidDebugger.EP_NAME.extensions) {
            if (androidDebugger.supportsProject(project)) {
                invokeLater { closeOldSessionAndRun(androidDebugger, device.getClient(packageName)) }
                break
            }
        }
    }

    private fun debuggerCanBeAttached() = AndroidDebugger.EP_NAME.extensions.size != 0 && device.getClient(packageName) != null

    private fun closeOldSessionAndRun(androidDebugger: AndroidDebugger<*>, client: Client) {
        terminateRunSessions(client)
        androidDebugger.attachToClient(project, client)
    }

    // Disconnect any active run sessions to the same client
    private fun terminateRunSessions(selectedClient: Client) {
        val pid = selectedClient.clientData.pid

        // find if there are any active run sessions to the same client, and terminate them if so
        for (handler in ExecutionManager.getInstance(project).runningProcesses) {
            if (handler is AndroidProcessHandler) {
                val client = handler.getClient(selectedClient.device)
                if (client != null && client.clientData.pid == pid) {
                    handler.detachProcess()
                    handler.notifyTextAvailable("Disconnecting run session: a new debug session will be established.\n", ProcessOutputTypes.STDOUT)
                    break
                }
            }
        }
    }


}
