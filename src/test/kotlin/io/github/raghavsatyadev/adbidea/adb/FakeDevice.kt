package io.github.raghavsatyadev.adbidea.adb

import com.android.ddmlib.IDevice
import java.lang.reflect.Proxy

data class FakeDevice(private val serialNumber: String) : IDevice by stub() {
    override fun getSerialNumber(): String {
        return serialNumber
    }
}

inline fun <reified T : Any> stub(): T =
    Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, _, _ ->
        throw NotImplementedError()
    } as T
