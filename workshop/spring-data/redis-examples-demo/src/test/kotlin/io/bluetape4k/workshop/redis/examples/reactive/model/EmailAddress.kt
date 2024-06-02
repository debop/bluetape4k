package io.bluetape4k.workshop.redis.examples.reactive.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

/**
 * Json Serialize/Deserialize 시에 Class 정보를 포함하는 예
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
data class EmailAddress(
    val address: String = "",
): Serializable
