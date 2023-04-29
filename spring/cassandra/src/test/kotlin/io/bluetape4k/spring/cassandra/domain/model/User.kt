package io.bluetape4k.spring.cassandra.domain.model

import java.io.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.cassandra.core.mapping.Table

@Table("users")
data class User(
    @field:Id var
    id: String = "",

    var firstname: String? = null,
    var lastname: String? = null,
): Serializable
