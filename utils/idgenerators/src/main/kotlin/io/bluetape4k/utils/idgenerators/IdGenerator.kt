package io.bluetape4k.utils.idgenerators

import io.bluetape4k.collections.eclipse.FastList
import io.bluetape4k.core.assertPositiveNumber

interface IdGenerator<ID> {

    fun nextId(): ID

    fun nextIdAsString(): String

    fun nextIds(size: Int): List<ID> {
        size.assertPositiveNumber("size")
        return FastList(size) { nextId() }
    }

    fun nextIdsAsString(size: Int): List<String> {
        size.assertPositiveNumber("size")
        return FastList(size) { nextIdAsString() }
    }
}
