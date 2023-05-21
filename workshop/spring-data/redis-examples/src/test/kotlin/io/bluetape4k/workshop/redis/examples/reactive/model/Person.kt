package io.bluetape4k.workshop.redis.examples.reactive.model

import java.io.Serializable

data class Person(
    val firstname: String = "",
    val lastname: String = "",
): Serializable
