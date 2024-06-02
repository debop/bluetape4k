package io.bluetape4k.workshop.webflux.model

import java.math.BigDecimal
import java.time.Instant

class Command

data class Event(
    val id: String,
    val data: List<Quote>,
)

data class Quote(
    val ticker: String,
    val price: BigDecimal,
    val instant: Instant = Instant.now(),
)
