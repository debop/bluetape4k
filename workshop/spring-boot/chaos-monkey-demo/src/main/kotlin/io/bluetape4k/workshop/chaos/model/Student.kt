package io.bluetape4k.workshop.chaos.model

import java.io.Serializable

data class Student(
    var id: Int? = null,
    var name: String? = null,
    var passportNumber: String? = null,
): Serializable
