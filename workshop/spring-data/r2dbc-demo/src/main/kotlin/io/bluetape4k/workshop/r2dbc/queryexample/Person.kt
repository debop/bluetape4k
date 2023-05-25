package io.bluetape4k.workshop.r2dbc.queryexample

import org.springframework.data.annotation.Id
import java.io.Serializable

data class Person(
    val firstname: String,
    val lastname: String,
    val age: Int,
): Serializable {

    @Id
    var id: Int? = null

    val hasId: Boolean get() = id != null

}
