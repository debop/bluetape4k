package io.bluetape4k.workshop.redis.cluster.service

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.redis.cluster.AbstractRedisClusterTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.RedisConnectionFactory

class NumberServiceTest(
    @Autowired private val numberService: NumberService,
    @Autowired private val connectionFactory: RedisConnectionFactory,
): AbstractRedisClusterTest() {

    companion object: KLogging()

    @BeforeEach
    fun beforeEach() {
        connectionFactory.clusterConnection.use { conn ->
            conn.serverCommands().flushDb()
        }
    }

    @Test
    fun `context loading`() {
        numberService.shouldNotBeNull()
    }

    @Test
    fun `operation to redis cluster`() {
        numberService.get(0).shouldBeNull()

        for (i in 1 until 100) {
            numberService.multiplyAndSave(i)
        }

        for (i in 1 until 100) {
            numberService.get(i) shouldBeEqualTo (i * 2)
        }
    }
}
