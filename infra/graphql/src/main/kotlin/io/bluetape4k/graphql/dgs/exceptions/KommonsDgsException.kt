package io.bluetape4k.graphql.dgs.exceptions

import com.netflix.graphql.dgs.exceptions.DgsException
import com.netflix.graphql.types.errors.ErrorType

open class KommonsDgsException(
    message: String,
    cause: Exception? = null,
    errorType: ErrorType = ErrorType.UNKNOWN,
): DgsException(message, cause, errorType)
