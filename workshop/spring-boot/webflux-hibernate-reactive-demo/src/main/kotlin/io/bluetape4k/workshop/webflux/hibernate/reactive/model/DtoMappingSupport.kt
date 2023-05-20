package io.bluetape4k.workshop.webflux.hibernate.reactive.model

fun Customer.toDto(): CustomerDto {
    return CustomerDto(
        id = this.identifier,
        name = this.name,
        cityName = this.city?.name
    )
}
