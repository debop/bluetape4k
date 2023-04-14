package io.bluetape4k.utils.idgenerators

import io.bluetape4k.core.assertPositiveNumber

/**
 * [Long] 수형의 Unique Identifier를 생성하는 Generator
 */
interface LongIdGenerator {

    fun nextId(): Long

    fun nextIdAsString(): String = nextId().toString(ALPHA_NUMERIC_BASE)

    fun nextIds(size: Int): Sequence<Long>

    fun nextIdsAsString(size: Int): Sequence<String> {
        size.assertPositiveNumber("size")
        return nextIds(size).map { it.toString(ALPHA_NUMERIC_BASE) }
    }
}
