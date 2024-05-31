package io.bluetape4k.idgenerators.uuid

import com.fasterxml.uuid.Generators
import io.bluetape4k.codec.Url62
import io.bluetape4k.idgenerators.IdGenerator
import java.util.*

/**
 * Implementation of UUID generator that uses one of name-based generation methods (versions 3 (MD5) and 5 (SHA1)).
 * As all JUG provided implementations, this generator is fully thread-safe; access
 * to digester is synchronized as necessary.
 */
class NamebasedUuidGenerator: IdGenerator<UUID> {

    private val namebasedUuid = Generators.nameBasedGenerator()
    private val randomUuid = Generators.randomBasedGenerator(Random(System.currentTimeMillis()))

    override fun nextId(): UUID = nextIdInternal()

    override fun nextIdAsString(): String = Url62.encode(nextIdInternal())

    private fun nextIdInternal(): UUID {
        return namebasedUuid.generate(randomUuid.generate().toString())
    }
}
