package io.bluetape4k.spring.cassandra.domain.model

import java.io.Serializable
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table
data class TypeWithKeyClass(
    @field:PrimaryKey
    val key: CompositeKey? = null,
): Serializable
