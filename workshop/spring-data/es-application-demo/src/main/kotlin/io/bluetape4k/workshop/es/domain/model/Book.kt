package io.bluetape4k.workshop.es.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.io.Serializable

@Document(indexName = "books")
data class Book(
    val title: String,
    val authorName: String,
    val publicationYear: Int,
    val isbn: String,
    @Id var id: String? = null,
): Serializable
