package com.developerphil.adbidea.debugger

import com.android.ddmlib.Client
import com.android.ddmlib.IDevice
import com.android.tools.idea.run.AndroidProcessHandler
import com.android.tools.idea.run.editor.AndroidDebugger
import com.developerphil.adbidea.compatibility.BackwardCompatibleGetter
import com.developerphil.adbidea.invokeLater
import com.developerphil.adbidea.waitUntil
import com.intellij.execution.ExecutionManager
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.project.Project
import org.joor.Reflect.on

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
        AttachToClient(androidDebugger, project, client).get()
    }

    // Disconnect any active run sessions to the same client
    private fun terminateRunSessions(selectedClient: Client) {
        val pid = PidGetter(selectedClient).get()

        // find if there are any active run sessions to the same client, and terminate them if so
        for (handler in ExecutionManager.getInstance(project).getRunningProcesses()) {
            if (handler is AndroidProcessHandler) {
                val client = handler.getClient(selectedClient.device)
                if (client != null && PidGetter(client).get() == pid) {
                    handler.detachProcess()
                    handler.notifyTextAvailable("Disconnecting run session: a new debug session will be established.\n", ProcessOutputTypes.STDOUT)
                    break
                }
            }
        }
    }
}

/**
 * To remove when 4.0 hits stable.
 */
class PidGetter(private val client: Client) : BackwardCompatibleGetter<Int>() {
    override fun getCurrentImplementation() = client.clientData.pid

    override fun getPreviousImplementation() = on(client).call("getClientData").call("getPid").get<Int>()!!
}

/**
 * To remove when 4.0 hits stable.
 */
class AttachToClient(private val androidDebugger: AndroidDebugger<*>,
                     private val project: Project,
                     private val client: Client) : BackwardCompatibleGetter<Unit>() {
    override fun getCurrentImplementation() {
        androidDebugger.attachToClient(project, client, null)
    }

    override fun getPreviousImplementation() {
        on(androidDebugger).call("attachToClient", project, client)
    }
}
