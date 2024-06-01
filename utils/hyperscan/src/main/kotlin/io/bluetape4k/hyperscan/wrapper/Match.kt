package io.bluetape4k.hyperscan.wrapper

import java.io.Serializable

/**
 * Represents a match found during the scan
 *
 * @property startPosition the start position of the match
 * @property endPosition the end position of the match
 * @property matchedString matched string if SOM flag is set, otherwise empty string
 * @property matchedExpression the [Expression] object used to find the match
 */
data class Match(
    val startPosition: Int,
    val endPosition: Int,
    val matchedString: String,
    val matchedExpression: Expression,
): Serializable
