package io.bluetape4k.workshop.redis.examples.reactive.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
data class EmailAddress(
    val address: String,
): Serializable
