package io.bluetape4k.infra.graphql

import java.io.Serializable

interface LoggingContextProvider: Serializable {

    val contextMap: Map<String, String>?

    val contextMapOrEmpty: Map<String, String>
        get() = contextMap ?: emptyMap()
}
