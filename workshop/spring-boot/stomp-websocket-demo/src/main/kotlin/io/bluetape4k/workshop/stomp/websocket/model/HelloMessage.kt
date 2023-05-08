package io.bluetape4k.workshop.stomp.websocket.model

import java.io.Serializable

data class HelloMessage(
    val name: String = "",
): Serializable
