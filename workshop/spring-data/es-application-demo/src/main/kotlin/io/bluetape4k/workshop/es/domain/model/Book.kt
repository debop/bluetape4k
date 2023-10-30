package io.bluetape4k.workshop.es.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.io.Serializable

@Document(indexName = "books")
data class Book(
    @Id var id: String? = null,
    val title: String,
    val publicationYear: Int,
    val authorName: String? = null,
    val isbn: String,
): Serializable
