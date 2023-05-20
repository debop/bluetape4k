package io.bluetape4k.workshop.webflux.hibernate.reactive.model

import java.io.Serializable

data class CustomerDto(
    val id: Long,
    val name: String,
    val cityName: String? = null,
): Serializable
