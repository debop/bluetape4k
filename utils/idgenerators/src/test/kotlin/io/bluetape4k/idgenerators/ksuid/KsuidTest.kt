package io.bluetape4k.idgenerators.ksuid

import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.RepeatedTest

@RandomizedTest
class KsuidTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate ksuid`() {
        val ksuid = Ksuid.generate()

        log.debug { "Generated Ksuid=$ksuid" }
        log.debug { Ksuid.prettyString(ksuid) }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate ksuid multiple`() {
        val ids = List(100) { Ksuid.generate() }

        ids.distinct() shouldHaveSize ids.size
    }

}
