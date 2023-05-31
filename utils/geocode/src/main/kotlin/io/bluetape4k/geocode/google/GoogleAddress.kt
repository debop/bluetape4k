package io.bluetape4k.geocode.google

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.geocode.Address

class GoogleAddress(
    val placeId: String? = null,
    country: String? = null,
    city: String? = null,
    val detailAddress: String? = null,
    val zipCode: String? = null,
    val formattedAddress: String? = null,
): Address(country, city) {


    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("detailAddress", detailAddress)
            .add("zipCode", zipCode)
            .add("placeId", placeId)
            .add("formattedAddress", formattedAddress)
    }
}
