package io.bluetape4k.workshop.application.event.aspect

import org.springframework.context.ApplicationEvent

data class AspectEvent(
    val src: Any,
    val message: Any,
): ApplicationEvent(src)
