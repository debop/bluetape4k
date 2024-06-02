package io.bluetape4k.workshop.micrometer.model

data class Todo(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean,
)
