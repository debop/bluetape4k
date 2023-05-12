package io.bluetape4k.spring.cassandra.domain.model

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.unifiedMapOf
import io.bluetape4k.collections.eclipse.unifiedSetOf
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.io.Serializable

@Table("bookReference")
data class BookReference(
    @field:PrimaryKey
    val isbn: String = "",

    var title: String = "",
    var references: MutableSet<String> = unifiedSetOf(),
    var bookmarks: MutableList<String> = fastListOf(),
    var credits: MutableMap<String, String> = unifiedMapOf(),
): Serializable
