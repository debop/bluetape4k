package io.bluetape4k.openai.client.sse

import io.bluetape4k.support.toUtf8Bytes

/**
 * Server sent event
 *
 * @property data FlowEvent data
 */
data class ServerSentEvent(
    val data: String? = null,
) {
    companion object {
        private const val DONE_DATA = "[DONE]"
    }

    val isDone: Boolean
        get() = DONE_DATA.contentEquals(data, true)

    fun toBytes(): ByteArray = "data: $data\n\n".toUtf8Bytes()

}
