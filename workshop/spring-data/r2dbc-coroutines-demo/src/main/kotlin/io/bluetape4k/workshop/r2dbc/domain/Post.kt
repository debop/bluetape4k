package io.bluetape4k.workshop.r2dbc.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Table("posts")
data class Post(
    @Column("title")
    val title: String? = null,

    @Column("content")
    val content: String? = null,

    @Id
    val id: Long? = null,
): Serializable
