package io.bluetape4k.openai.clients.retrofit2.utils

import io.bluetape4k.support.toUtf8Bytes

/**
 * Simple Server Sent Event representation
 *
 * @property data event data
 */
data class SSE(val data: String) {

    companion object {
        private const val DONE = "[DONE]"
    }

    val isDone: Boolean get() = DONE.equals(data, ignoreCase = true)

    fun toBytes(): ByteArray {
        return "data: $data\n\n".toUtf8Bytes()
    }
}
