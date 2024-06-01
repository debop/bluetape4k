package io.bluetape4k.graphql.dgs.exceptions

import com.netflix.graphql.types.errors.ErrorType

open class KommonsDgsInternalException(
    message: String = "Internal error",
    cause: Exception? = null,
): KommonsDgsException(message, cause, ErrorType.INTERNAL) {

    constructor(cause: Exception): this(cause.message ?: "Internal error", cause)
}
