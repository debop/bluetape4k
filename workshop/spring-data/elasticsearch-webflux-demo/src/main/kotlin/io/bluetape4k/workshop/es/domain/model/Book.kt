package io.bluetape4k.workshop.es.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import java.io.Serializable

@Document(indexName = "books")
data class Book(
    @Field val title: String,
    val authorName: String,
    val publicationYear: Int,
    @Field val isbn: String,
    @Id var id: String? = null,
): Serializable
