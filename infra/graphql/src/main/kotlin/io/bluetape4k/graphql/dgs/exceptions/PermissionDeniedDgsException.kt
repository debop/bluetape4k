package io.bluetape4k.graphql.dgs.exceptions

import com.netflix.graphql.types.errors.ErrorType

open class PermissionDeniedDgsException(
    message: String = "Permission denied",
    cause: Exception? = null,
): Bluetape4kDgsException(message, cause, ErrorType.PERMISSION_DENIED) {

    constructor(cause: Exception): this(cause.message ?: "Permission denied", cause)
}
