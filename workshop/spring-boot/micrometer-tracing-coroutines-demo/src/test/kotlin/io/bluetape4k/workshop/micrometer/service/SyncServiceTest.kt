package io.bluetape4k.workshop.micrometer.service

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.micrometer.AbstractTracingTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SyncServiceTest(
    @Autowired private val service: SyncService,
): AbstractTracingTest() {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        service.shouldNotBeNull()
    }

    @Test
    fun `get name`() {
        service.getName().shouldNotBeEmpty()
    }

    @Test
    fun `get todo by id`() {
        val id = 42
        val todo = service.getTodo(id)
        log.debug { "todo: $todo" }
        todo.shouldNotBeNull()
        todo.id shouldBeEqualTo id
    }
}
