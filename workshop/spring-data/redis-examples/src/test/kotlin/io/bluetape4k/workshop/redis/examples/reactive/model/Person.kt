package io.bluetape4k.workshop.redis.examples.reactive.model

import java.io.Serializable

data class Person(
    var firstname: String = "",
    var lastname: String = "",
): Serializable
