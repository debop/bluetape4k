package io.bluetape4k.openai.client.model.file

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class File(
    @JsonProperty("id") val id: String,
    @JsonProperty("object") val objectType: String,
    @JsonProperty("bytes") val bytes: Long,
    @JsonProperty("created_at") val createdAt: Long,
    @JsonProperty("filename") val filename: String,
    @JsonProperty("purpose") val purpose: String,
    @JsonProperty("format") val format: String? = null,
    @JsonProperty("status") val status: FileStatus? = null,
    @JsonProperty("status_details") val statusDetails: String? = null,
): Serializable 
