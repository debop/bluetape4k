package io.bluetape4k.graphql.dgs.exceptions

import com.netflix.graphql.types.errors.ErrorType

open class Bluetape4kDgsInternalException(
    message: String = "Internal error",
    cause: Exception? = null,
): Bluetape4kDgsException(message, cause, ErrorType.INTERNAL) {

    constructor(cause: Exception): this(cause.message ?: "Internal error", cause)
}
