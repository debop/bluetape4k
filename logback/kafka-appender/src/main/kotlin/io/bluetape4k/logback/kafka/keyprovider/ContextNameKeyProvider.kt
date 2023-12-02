package io.bluetape4k.logback.kafka.keyprovider

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Context
import ch.qos.logback.core.CoreConstants
import io.bluetape4k.logback.kafka.utils.hashBytes

class ContextNameKeyProvider: AbstractKeyProvider<ILoggingEvent>() {

    private var contextNameHash: ByteArray? = null

    override fun setContext(context: Context) {
        super.setContext(context)
        val hostname = context.getProperty(CoreConstants.CONTEXT_NAME_KEY)

        if (hostname.isNullOrBlank()) {
            if (!errorWasShown) {
                addError("Context name could not be found. Please set ${CoreConstants.CONTEXT_NAME_KEY} property in logback context.")
                errorWasShown = true
            }
        } else {
            contextNameHash = hostname.hashBytes()
        }
    }

    override fun get(e: ILoggingEvent): ByteArray? {
        return contextNameHash
    }
}
