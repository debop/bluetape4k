package io.bluetape4k.workshop.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

@Document("processes")
data class Process(
    @Id val id: Int,
    val state: State = State.UNKNOWN,
    val transitionCount: Int = 0,
): Serializable
