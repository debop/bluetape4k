package io.bluetape4k.core

import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.RepeatedTest

@RandomizedTest
class StandardSupportTest {

    companion object : KLogging() {
        private const val REPEAT_SIZE = 10
    }


    @RepeatedTest(REPEAT_SIZE)
    fun `run safeLet`(@RandomValue p1: String?, @RandomValue p2: String?) {
        safeLet(p1, p2) { a, b ->
            a.shouldNotBeNull()
            b.shouldNotBeNull()
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `when all not null, should execute`(@RandomValue(type = String::class) strs: List<String?>) {
        strs.whenAllNotNull {
            it.size shouldBeEqualTo strs.filterNotNull().size
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `coalesce - find first not null element`(@RandomValue p1: String?, @RandomValue p2: String?) {
        coalesce(p1, p2) shouldBeEqualTo when {
            p1 != null -> p1
            p2 != null -> p2
            else -> null
        }
    }
}
