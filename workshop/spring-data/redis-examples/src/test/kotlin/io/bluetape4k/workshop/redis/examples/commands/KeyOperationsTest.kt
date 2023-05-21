package io.bluetape4k.workshop.redis.examples.commands

import io.bluetape4k.collections.stream.toFastList
import io.bluetape4k.coroutines.flow.toFastList
import io.bluetape4k.coroutines.support.asFlow
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.workshop.redis.examples.AbstractRedisTest
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.Cursor
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.serializer.StringRedisSerializer

class KeyOperationsTest(
    @Autowired private val connectionFactory: RedisConnectionFactory
) : AbstractRedisTest() {

    companion object : KLogging() {
        private val PREFIX: String = KeyOperationsTest::class.simpleName!!
        private val KEY_PATTERN = "$PREFIX*"
        private const val KEY_SIZE = 5000

        private const val REPEAT_SIZE = 3
    }

    private val connection by lazy { connectionFactory.connection }
    private val serializer = StringRedisSerializer()

    private fun generateRandomKeys(size: Int) {
        repeat(size) {
            connection.set("$PREFIX-$it".toUtf8Bytes(), Fakers.fixedString(128).toUtf8Bytes())
        }
    }

    @BeforeAll
    fun beforeAll() {
        generateRandomKeys(KEY_SIZE)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `keys - matching pattern`() {
        val keys = connection.keys(KEY_PATTERN.toUtf8Bytes())?.map { serializer.deserialize(it) }
        keys.shouldNotBeNull() shouldHaveSize KEY_SIZE
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `scan - matching pattern`() {
        val options: ScanOptions = ScanOptions.scanOptions()
            .match(KEY_PATTERN)
            .count(100L)
            .build()
        val cursor: Cursor<ByteArray> = connection.scan(options)

        val keys = cursor.stream().toFastList().map { serializer.deserialize(it) }
        keys shouldHaveSize KEY_SIZE
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `scan - matching pattern with flow`() = runSuspendWithIO {
        val options: ScanOptions = ScanOptions.scanOptions()
            .match(KEY_PATTERN)
            .count(100L)
            .build()
        val cursor: Cursor<ByteArray> = connection.scan(options)

        val keys = cursor.stream()
            .asFlow()
            .buffer(100)
            .map { serializer.deserialize(it) }
            .toFastList()
        keys shouldHaveSize KEY_SIZE
    }
}
