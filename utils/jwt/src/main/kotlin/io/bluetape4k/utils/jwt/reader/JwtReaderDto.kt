package io.bluetape4k.utils.jwt.reader

import java.io.Serializable

data class JwtReaderDto(
    val headers: Map<String, Any?> = mutableMapOf(),
    val claims: Map<String, Any?> = mutableMapOf(),
    val signature: String? = null,
): Serializable
