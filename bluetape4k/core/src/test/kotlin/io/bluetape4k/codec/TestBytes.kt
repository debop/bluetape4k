package io.bluetape4k.codec

import java.nio.ByteBuffer
import kotlin.random.Random

object TestBytes {

    val EMPTY = ByteArray(0)

    fun allLength1(): Iterable<ByteArray> {
        return iterable<ByteArray>(AllLength1())
    }

    fun allLength2(): Iterable<ByteArray> {
        return iterable<ByteArray>(AllLength2())
    }

    fun allLength3(): Iterable<ByteArray> {
        return iterable<ByteArray>(AllLength3())
    }

    fun randomBytes(size: Int): ByteArray {
        return Random.nextBytes(size)
    }

    private fun <T> iterable(itr: Iterator<T>): Iterable<T> {
        return Iterable { itr }
    }


    private class AllLength1: Iterator<ByteArray> {
        private val buf = ByteBuffer.allocate(1).put(0, Byte.MIN_VALUE)
        override fun hasNext(): Boolean {
            return buf[0] != Byte.MAX_VALUE
        }

        override fun next(): ByteArray {
            return buf.put(0, (buf[0] + 1).toByte()).array()
        }
    }


    private class AllLength2: Iterator<ByteArray> {
        private val buf = ByteBuffer.allocate(2).putShort(0, Short.MIN_VALUE)
        override fun hasNext(): Boolean {
            return buf.getShort(0) != Short.MAX_VALUE
        }

        override fun next(): ByteArray {
            return buf.putShort(0, (buf.getShort(0) + 1).toShort()).array()
        }
    }


    private class AllLength3: Iterator<ByteArray> {
        private val buf = ByteBuffer.allocate(3)

        init {
            putMedium(0x800000)
        }

        private val medium: Int
            get() = buf.getShort(0).toInt() shl 8 or (buf[2].toInt() and 0xFF)

        private fun putMedium(value: Int): ByteBuffer {
            return buf.putShort(0, (value shr 8).toShort()).put(2, value.toByte())
        }

        override fun hasNext(): Boolean {
            return medium != 0x7FFFFF
        }

        override fun next(): ByteArray {
            return putMedium(medium + 1).array()
        }
    }
}
