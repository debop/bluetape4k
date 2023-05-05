package io.bluetape4k.infra.graphql.dgs.exceptions

import com.netflix.graphql.dgs.exceptions.DgsException
import com.netflix.graphql.types.errors.ErrorType

open class BluetapeDgsException(
    message: String,
    cause: Exception? = null,
    errorType: ErrorType = ErrorType.UNKNOWN,
): DgsException(message, cause, errorType)
