package io.bluetape4k.logback.kafka.keycreator

import ch.qos.logback.classic.spi.ILoggingEvent
import io.bluetape4k.logback.kafka.utils.toHashBytes

class ThreadNameKeyCreator: AbstractKeyCreator<ILoggingEvent>() {

    override fun create(e: ILoggingEvent): ByteArray? {
        return e.threadName.toHashBytes()
    }
}
