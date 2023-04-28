package io.bluetape4k.spring.cassandra.domain.model

import java.io.Serializable
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("bookReference")
data class BookReference(
    @field:PrimaryKey
    val isbn: String = "",

    var title: String = "",
    var references: MutableSet<String> = mutableSetOf(),
    var bookmarks: MutableList<String> = mutableListOf(),
    var credits: MutableMap<String, String> = mutableMapOf(),
): Serializable
