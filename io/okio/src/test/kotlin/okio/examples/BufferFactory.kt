package okio.examples

import io.bluetape4k.okio.tests.TestUtil.bufferWithRandomSegmentLayout
import io.bluetape4k.okio.tests.TestUtil.bufferWithSegments
import okio.Buffer
import kotlin.random.Random

enum class BufferFactory {

    EMPTY {
        override fun newBuffer(): Buffer {
            return Buffer()
        }
    },

    SMALL_BUFFER {
        override fun newBuffer(): Buffer {
            return Buffer().writeUtf8("abcde")
        }
    },
    SMALL_SEGMENTED_BUFFER {
        override fun newBuffer(): Buffer {
            return bufferWithSegments("abc", "defg", "hijkl")
        }
    },
    LARGE_BUFFER {
        override fun newBuffer(): Buffer {
            val largeByteArray = ByteArray(512 * 1024)
            Random.nextBytes(largeByteArray)
            return Buffer().write(largeByteArray)
        }
    },

    LARGE_BUFFER_WITH_RANDOM_LAYOUT {
        override fun newBuffer(): Buffer {
            val largeByteArray = ByteArray(512 * 1024)
            Random.nextBytes(largeByteArray)
            return bufferWithRandomSegmentLayout(Random.Default, largeByteArray)
        }
    };

    abstract fun newBuffer(): Buffer
}
