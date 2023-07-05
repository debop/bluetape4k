package io.bluetape4k.data.jdbc.model

import java.io.Serializable
import java.sql.Timestamp


data class TestBean(
    val id: Int,
    val description: String? = null,
    val createdAt: Timestamp? = null,
): Serializable
