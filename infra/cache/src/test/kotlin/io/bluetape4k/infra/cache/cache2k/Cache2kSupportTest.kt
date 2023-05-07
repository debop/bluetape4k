package io.bluetape4k.infra.cache.cache2k

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class Cache2kSupportTest {

    @Test
    fun `create cache2k cache`() {
        val cache = cache2k<String, Any> {
            name("simple")
            expireAfterWrite(10, TimeUnit.SECONDS)
        }.build()

        cache.name shouldBeEqualTo "simple"
        cache.putIfAbsent("item1", 1).shouldBeTrue()
        cache.putIfAbsent("item1", 2).shouldBeFalse()

        val value = cache.computeIfAbsent("item2") {
            Thread.sleep(100)
            42
        }

        value shouldBeEqualTo 42
    }
}
