package io.bluetape4k.utils.idgenerators.uuid

import java.util.UUID

/**
 * Timebased UUID 를 제공하는 Utility
 */
object TimebasedUuid {

    private val generator: TimebasedUuidGenerator by lazy { TimebasedUuidGenerator() }

    /**
     * Timebased UUID 를 생성합니다.
     */
    fun nextUUID(): UUID = generator.nextUUID()

    fun nextUUIDs(size: Int): Sequence<UUID> = generator.nextUUIDs(size)

    fun nextBase62String(): String = generator.nextBase62String()

    fun nextBase62Strings(size: Int): Sequence<String> = generator.nextBase62Strings(size)
}
