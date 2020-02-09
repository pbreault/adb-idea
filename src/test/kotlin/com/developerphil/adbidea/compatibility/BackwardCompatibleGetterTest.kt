package com.developerphil.adbidea.compatibility

import com.google.common.truth.Truth.assertThat
import org.joor.ReflectException
import org.junit.Assert.fail
import org.junit.Test

class BackwardCompatibleGetterTest {
    @Test
    fun onlyCallCurrentImplementationWhenItIsValid() {
        var value = false
        object : BackwardCompatibleGetter<Boolean>() {
            override fun getCurrentImplementation(): Boolean {
                value = true
                return true
            }

            override fun getPreviousImplementation(): Boolean {
                fail("should not be called")
                return true
            }
        }.get()
        assertThat(value).isTrue()
    }

    @Test
    fun callPreviousImplementationWhenCurrentThrowsErrors() {
        expectPreviousImplementationIsCalledFor(ClassNotFoundException())
        expectPreviousImplementationIsCalledFor(NoSuchMethodException())
        expectPreviousImplementationIsCalledFor(NoSuchFieldException())
        expectPreviousImplementationIsCalledFor(LinkageError())
        expectPreviousImplementationIsCalledFor(ReflectException())
    }

    @Test(expected = RuntimeException::class)
    fun throwExceptionsWhenTheyAreNotRelatedToBackwardCompatibility() {
        object : BackwardCompatibleGetter<Boolean>() {
            override fun getCurrentImplementation(): Boolean {
                throw RuntimeException("exception!")
            }

            override fun getPreviousImplementation(): Boolean {
                fail("should not be called")
                return false
            }
        }.get()
    }

    private fun expectPreviousImplementationIsCalledFor(throwable: Throwable) {
        var value = false
        object : BackwardCompatibleGetter<Boolean>() {
            override fun getCurrentImplementation(): Boolean {
                throw throwable
            }

            override fun getPreviousImplementation(): Boolean {
                value = true
                return true
            }
        }.get()

        assertThat(value).isTrue()
    }
}