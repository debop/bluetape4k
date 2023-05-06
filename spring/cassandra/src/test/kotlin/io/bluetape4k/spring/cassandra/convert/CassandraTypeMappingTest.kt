package io.bluetape4k.spring.cassandra.convert

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.spring.cassandra.AbstractCassandraTest
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.CassandraOperations

@SpringBootTest(classes = [ConvertTestConfiguration::class])
class CassandraTypeMappingTest(
    @Autowired private val operations: CassandraOperations,
): AbstractCassandraTest() {

    companion object: KLogging() {
        private val initialized = atomic(false)
    }

    @BeforeEach
    fun beforeEach() {
        if (initialized.compareAndSet(false, true)) {
            // TODO: 초기화
        }
    }

    @Test
    fun `context loading`() {
        operations.shouldNotBeNull()
        session.shouldNotBeNull()
        log.info { "Current keyspace=${session.keyspace}" }
    }
}
