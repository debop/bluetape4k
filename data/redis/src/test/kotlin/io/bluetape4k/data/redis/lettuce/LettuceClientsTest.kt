package io.bluetape4k.data.redis.lettuce

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class LettuceClientsTest: AbstractLettuceTest() {

    companion object: KLogging()

    @Test
    fun `connect to redis server`() {
        val commands = LettuceClients.commands(client)

        val result = commands.ping()
        result shouldBeEqualTo "PONG"
    }
}
