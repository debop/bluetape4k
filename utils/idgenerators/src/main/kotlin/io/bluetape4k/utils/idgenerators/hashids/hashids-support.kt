package io.bluetape4k.utils.idgenerators.hashids

import io.bluetape4k.core.assertNotBlank
import io.bluetape4k.support.toLongArray
import io.bluetape4k.support.toUUID
import java.util.UUID

fun Hashids.encodeUUID(uuid: UUID): String = encode(*uuid.toLongArray())

fun Hashids.decodeUUID(hash: String): UUID {
    hash.assertNotBlank("hash")
    return decode(hash).toUUID()
}
