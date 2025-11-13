package io.github.raghavsatyadev.adbidea

fun waitUntil(timeoutMillis: Long = 30000L, step: Long = 100L, condition: () -> Boolean) {
    val endTime = System.currentTimeMillis() + timeoutMillis
    while (System.currentTimeMillis() < endTime) {
        if (condition()) {
            return
        }
        Thread.sleep(step)
    }
}
