package io.bluetape4k.data.hibernate.mapping.embeddable

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
data class EmbeddableAddress(
    var street: String,
    var city: String,
    var zipcode: String,
): Serializable
