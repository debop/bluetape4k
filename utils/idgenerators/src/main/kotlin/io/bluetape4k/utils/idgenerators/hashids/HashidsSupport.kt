package io.bluetape4k.utils.idgenerators.hashids

import io.bluetape4k.support.assertNotBlank
import io.bluetape4k.support.toLongArray
import io.bluetape4k.support.toUUID
import java.util.*

fun Hashids.encodeUUID(uuid: UUID): String = encode(*uuid.toLongArray())

fun Hashids.decodeUUID(hash: String): UUID {
    hash.assertNotBlank("hash")
    return decode(hash).toUUID()
}
