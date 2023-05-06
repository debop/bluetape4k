package io.bluetape4k.utils.ahocorasick.interval

import io.bluetape4k.core.ValueObject

interface Intervalable: Comparable<Intervalable>, ValueObject {

    val start: Int
    val end: Int

    val size: Int get() = end - start + 1
}
