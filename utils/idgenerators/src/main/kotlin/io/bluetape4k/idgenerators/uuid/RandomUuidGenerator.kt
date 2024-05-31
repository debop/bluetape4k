package io.bluetape4k.idgenerators.uuid

import com.fasterxml.uuid.Generators
import io.bluetape4k.codec.Url62
import io.bluetape4k.idgenerators.IdGenerator
import java.util.*

class RandomUuidGenerator(
    random: Random = Random(System.currentTimeMillis()),
): IdGenerator<UUID> {

    // VERSION 4
    private val generator = Generators.randomBasedGenerator(random)

    override fun nextId(): UUID = generator.generate()

    override fun nextIdAsString(): String = Url62.encode(nextId())
}
