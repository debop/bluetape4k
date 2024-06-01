package io.bluetape4k.retrofit2

import java.io.IOException

fun Throwable.toIOException(): IOException {
    return when (this) {
        is IOException -> this
        else           -> {
            val message = this.message ?: this.toString()
            IOException(message, this)
        }
    }
}
