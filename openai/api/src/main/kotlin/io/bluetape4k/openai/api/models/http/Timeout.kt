package io.bluetape4k.openai.api.models.http

import java.io.Serializable
import kotlin.time.Duration

data class Timeout(
    val request: Duration? = null,
    val connect: Duration? = null,
    val socket: Duration? = null,
): Serializable
