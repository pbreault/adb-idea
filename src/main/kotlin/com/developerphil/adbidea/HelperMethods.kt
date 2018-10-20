package com.developerphil.adbidea

import com.intellij.openapi.application.ApplicationManager
import org.jdesktop.swingx.util.OS
import javax.swing.JOptionPane

fun waitUntil(timeoutMillis: Long = 30000L, step: Long = 100L, condition: () -> Boolean) {
    val endTime = System.currentTimeMillis() + timeoutMillis
    while (System.currentTimeMillis() < endTime) {
        if (condition()) {
            return
        }
        Thread.sleep(step)
    }
}

fun invokeLater(runnable: () -> Unit) {
    ApplicationManager.getApplication().invokeLater(runnable)
}

fun showErrorMsg(msg:String){
    JOptionPane.showMessageDialog(null, msg,"Error", JOptionPane.ERROR_MESSAGE)
}


fun openFileExplorer(path: String){
    if (OS.isWindows()) {
        Runtime.getRuntime().exec(arrayOf("cmd", "/C", "start $path"))
    }else if (OS.isMacOSX()) {
        Runtime.getRuntime().exec("open $path -a")
    }else if (OS.isLinux()){
        Runtime.getRuntime().exec("nautilus $path")
    }
}