package io.bluetape4k.workshop.mongo.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import java.io.Serializable

data class Person @PersistenceCreator constructor(
    @Id val id: String?,
    val firstname: String? = "Walter",
    val lastname: String? = "",
): Serializable {

    constructor(firstname: String?, lastname: String?): this(null, firstname, lastname)
}
