package io.bluetape4k.codec

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.logging.KLogging
import kotlin.random.Random

object RadixCoders: KLogging() {

    val U8: List<RadixCoder<ByteArray>> by lazy {
        (2..256).filter { it % 2 == 0 }.map { RadixCoder.u8(it) }
    }

    val U16: List<RadixCoder<ShortArray>> by lazy {
        val u16 = fastListOf<RadixCoder<ShortArray>>()
        var i = 2
        while (i <= 0x10000) {
            u16.add(RadixCoder.u16(i))
            i += 2 + Random.nextInt(2000, 10000)
        }
        u16
    }

    val ALL: List<RadixCoder<*>> by lazy { U8 + U16 }
}
