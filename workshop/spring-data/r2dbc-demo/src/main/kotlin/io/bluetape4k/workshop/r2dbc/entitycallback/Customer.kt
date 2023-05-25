package io.bluetape4k.workshop.r2dbc.entitycallback

import org.springframework.data.annotation.Id
import java.io.Serializable

data class Customer(
    val firstname: String,
    val lastname: String,
): Serializable {

    @Id
    var id: Long? = null

    val hasId: Boolean get() = id != null

    fun withId(id: Long): Customer = copy().apply { this.id = id }
}
