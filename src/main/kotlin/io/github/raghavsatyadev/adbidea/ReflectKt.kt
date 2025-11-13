package io.github.raghavsatyadev.adbidea

import org.joor.Reflect

inline fun <reified T> on() = Reflect.onClass(T::class.java)

inline fun <reified T> Reflect.asType() = this.`as`(T::class.java)
