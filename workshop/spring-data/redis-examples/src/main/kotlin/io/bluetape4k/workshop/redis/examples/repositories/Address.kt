package io.bluetape4k.workshop.redis.examples.repositories

import org.springframework.data.geo.Point
import org.springframework.data.redis.core.index.GeoIndexed
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable


data class Address(
    @Indexed
    var city: String,

    var country: String,

    @GeoIndexed
    var location: Point? = null,

    ): Serializable
