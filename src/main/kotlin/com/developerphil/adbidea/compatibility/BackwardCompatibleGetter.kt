package com.developerphil.adbidea.compatibility

import org.joor.ReflectException

/**
 * Abstracts the logic to call the current implementation and fall back on reflection for previous versions
 */
abstract class BackwardCompatibleGetter<T> {
    fun get(): T {
        return try {
            getCurrentImplementation()
        } catch (error: LinkageError) {
            getPreviousImplementation()
        } catch (e: Throwable) {
            if (isReflectiveException(e)) {
                getPreviousImplementation()
            } else {
                throw RuntimeException(e)
            }
        }
    }

    private fun isReflectiveException(t: Throwable): Boolean {
        return t is ClassNotFoundException ||
                t is NoSuchFieldException ||
                t is LinkageError ||
                t is NoSuchMethodException ||
                t is ReflectException
    }

    abstract fun getCurrentImplementation() : T

    abstract fun getPreviousImplementation() : T
}