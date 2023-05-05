package io.bluetape4k.infra.graphql.dgs.exceptions

import com.netflix.graphql.types.errors.ErrorType

open class BluetapeDgsInternalException(
    message: String = "Internal error",
    cause: Exception? = null,
): BluetapeDgsException(message, cause, ErrorType.INTERNAL) {

    constructor(cause: Exception): this(cause.message ?: "Internal error", cause)
}
