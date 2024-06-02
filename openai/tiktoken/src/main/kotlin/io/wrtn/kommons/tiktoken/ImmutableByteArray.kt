package io.bluetape4k.tiktoken

import io.bluetape4k.logging.KLogging

/**
 * 변경 불가능한 [ByteArray]를 나타내는 클래스입니다.
 */
internal class ImmutableByteArray private constructor(
    private val array: ByteArray,
) {
    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(text: String): ImmutableByteArray {
            return invoke(text.toByteArray(Charsets.UTF_8))
        }

        @JvmStatic
        operator fun invoke(bytes: ByteArray): ImmutableByteArray {
            return ImmutableByteArray(bytes.clone())
        }
    }

    val size: Int get() = array.size

    /**
     * Returns the bytes of this array from startIndex (inclusive) to endIndex (exclusive). The returned array is a copy
     * of the original array.
     *
     * @param startIndex the index from which to start copying (inclusive)
     * @param endIndex   the index at which to stop copying (exclusive)
     * @return a new {@link ImmutableByteArray} containing the bytes from startIndex (inclusive) to endIndex (exclusive)
     * @throws IllegalArgumentException if startIndex is out of bounds, endIndex is out of bounds or endIndex is less than
     *                                  startIndex
     */
    fun getBytesBetween(startIndex: Int, endIndex: Int): ImmutableByteArray {
        require(startIndex in 0 until size) {
            "endIndex out of bounds: $startIndex (0 until $size)"
        }
        require(endIndex in 0..size) {
            "endIndex out of bounds: $endIndex (0 .. $size)"
        }
        require(startIndex < endIndex) {
            "startIndex must be less than endIndex: $startIndex <= $endIndex"
        }

        return ImmutableByteArray(array.copyOfRange(startIndex, endIndex))
    }

    /**
     * Returns a copy of the raw array backing this [ImmutableByteArray].
     */
    fun getRawArray(): ByteArray = array.clone()

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false

        return other is ImmutableByteArray && array.contentEquals(other.array)
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }

    override fun toString(): String {
        return array.contentToString()
    }
}
