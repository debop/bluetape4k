package io.bluetape4k.hyperscan.wrapper

/**
 * Represents a compiler error due to an invalid expression
 *
 * @property failedExpression the failed expression object
 * @param message the error message
 */
class CompileErrorException(
    message: String,
    val failedExpression: Expression?,
): RuntimeException(message)
