package io.bluetape4k.workshop.elasticsearch.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import java.io.Serializable

@Document(indexName = "conference-index")
data class Conference(
    var name: String,
    @Field(type = FieldType.Date) var date: String,
    val location: GeoPoint? = null,
    val keywords: MutableList<String> = mutableListOf(),
    @Id var id: String? = null,
): Serializable
