package io.bluetape4k.okio

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeNear

abstract class AbstractOkioTest {

    companion object: KLogging()

    protected fun now(): Double = System.nanoTime() / 1_000_000.0

    protected fun assertElapsed(duration: Double, start: Double) {
        (now() - start - 200.0).shouldBeNear(duration, 250.0)
    }

}
