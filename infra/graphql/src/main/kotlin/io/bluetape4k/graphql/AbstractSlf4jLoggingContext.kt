package io.bluetape4k.graphql

import org.slf4j.MDC

abstract class AbstractSlf4jLoggingContext: LoggingContextProvider {

    override val contextMap: Map<String, String>?
        get() = MDC.getCopyOfContextMap()

}
