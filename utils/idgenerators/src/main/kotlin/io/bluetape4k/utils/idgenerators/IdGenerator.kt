package io.bluetape4k.utils.idgenerators

import io.bluetape4k.support.assertPositiveNumber

interface IdGenerator<ID> {

    fun nextId(): ID

    fun nextIdAsString(): String

    fun nextIds(size: Int): Sequence<ID> = sequence {
        size.assertPositiveNumber("size")
        repeat(size) {
            yield(nextId())
        }
    }

    fun nextIdsAsString(size: Int): Sequence<String> = sequence {
        size.assertPositiveNumber("size")
        repeat(size) {
            yield(nextIdAsString())
        }
    }
}
