package com.developerphil.adbidea.adb

import com.android.ddmlib.*
import com.android.ddmlib.log.LogReceiver
import com.android.sdklib.AndroidVersion
import java.io.File
import java.lang.reflect.Proxy
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

data class FakeDevice(private val serialNumber: String) : IDevice by stub() {
    override fun getSerialNumber(): String {
        return serialNumber
    }
}

inline fun <reified T : Any> stub(): T = Proxy.newProxyInstance(
    T::class.java.classLoader,
    arrayOf(T::class.java)
) { _, _, _ -> throw NotImplementedError() } as T