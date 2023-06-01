package io.bluetape4k.data.r2dbc.query

import java.io.Serializable

class Query(
    val sqlBuffer: StringBuilder,
    val parameters: Map<String, Any?>,
): Serializable {

    val sql: String by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
        sqlBuffer.toString().trim()
    }
}
