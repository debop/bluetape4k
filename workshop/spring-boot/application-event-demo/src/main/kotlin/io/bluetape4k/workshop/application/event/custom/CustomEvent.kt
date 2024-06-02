package io.bluetape4k.workshop.application.event.custom

import org.springframework.context.ApplicationEvent

data class CustomEvent(
    private val src: Any,
    val message: String,
): ApplicationEvent(src) {
    override fun toString(): String = "CustomEvent(src=${src.javaClass.name}, message=$message)"
}
