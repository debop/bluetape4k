package io.bluetape4k.utils.idgenerators.uuid

import com.fasterxml.uuid.Generators
import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.utils.idgenerators.IdGenerator
import java.util.*

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


    object Default: IdGenerator<UUID> {
        private val generator by lazy { Generators.timeBasedGenerator() }

        override fun nextId(): UUID {
            return generator.generate()
        }

        override fun nextIdAsString(): String {
            return nextId().encodeBase62()
        }
    }


    // Version 6
    object Reordered: IdGenerator<UUID> {
        private val generator by lazy { Generators.timeBasedReorderedGenerator() }

        override fun nextId(): UUID {
            return generator.generate()
        }

        override fun nextIdAsString(): String {
            return nextId().encodeBase62()
        }
    }

    // Version 7
    object Epoch: IdGenerator<UUID> {
        private val generator by lazy { Generators.timeBasedEpochGenerator() }

        override fun nextId(): UUID {
            return generator.generate()
        }

        override fun nextIdAsString(): String {
            return nextId().encodeBase62()
        }
    }
}
