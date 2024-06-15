package io.bluetape4k.okio

import okio.BufferedSink
import okio.BufferedSource

class BufferedSourceTest {

    class Pipe(
        var sink: BufferedSink,
        var source: BufferedSource,
    )

    interface Factory {
        fun pipe(): Pipe
        val isOneByteAtTime: Boolean

        companion object {
            val BUFFER: Factory = object: Factory {
                override fun pipe(): Pipe {
                    TODO("Not yet implemented")
                }

                override val isOneByteAtTime: Boolean
                    get() = TODO("Not yet implemented")

            }

        }
    }
}
