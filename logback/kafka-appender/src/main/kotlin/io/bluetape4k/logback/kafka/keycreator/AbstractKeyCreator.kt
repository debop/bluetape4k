package io.bluetape4k.logback.kafka.keycreator

import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.spi.LifeCycle

abstract class AbstractKeyCreator<E>: ContextAwareBase(), KeyCreator<E>, LifeCycle {

    protected var errorWasShown: Boolean = false

    override fun start() {
        errorWasShown = false
    }

    override fun stop() {
        errorWasShown = false
    }

    override fun isStarted(): Boolean = true
}
