package io.bluetape4k.examples.cassandra.reactive.auditing

import io.bluetape4k.examples.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import kotlinx.coroutines.delay
import org.amshove.kluent.`should be in range`
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant

@SpringBootTest(classes = [AuditingTestConfiguration::class])
class AuditingTest(
    @Autowired private val repository: OrderRepository,
    @Autowired private val customRepo: CustomAuditingRepository,
): AbstractCassandraCoroutineTest("auditing") {

    companion object: KLogging()

    @BeforeEach
    fun setup() = runSuspendWithIO {
        repository.deleteAll()
    }

    @Test
    fun `should update auditor`() = runSuspendWithIO {
        val order = Order("4711")
        order.createdAt.shouldBeNull()
        order.isNew.shouldBeTrue()

        val instantRange = Instant.now().minusSeconds(60)..Instant.now().plusSeconds(60)

        val actual = repository.save(order)
        log.info { "Actual createdAt=${actual.createdAt}, lastModifiedAt=${actual.lastModifiedAt}" }

        actual.createdBy shouldBeEqualTo "the-current-user"
        actual.createdAt.shouldNotBeNull().`should be in range`(instantRange)

        actual.lastModifiedBy shouldBeEqualTo "the-current-user"
        actual.lastModifiedAt.shouldNotBeNull().`should be in range`(instantRange)

        delay(100)

        val loaded = repository.findById("4711")!!
        log.info { "loaded createdAt=${loaded.createdAt}, lastModifiedAt=${loaded.lastModifiedAt}" }
        loaded.isNew.shouldBeFalse()

        val ssaved = repository.save(loaded)
        log.info { "Actual createdAt=${actual.createdAt}, lastModifiedAt=${actual.lastModifiedAt}" }

        ssaved.createdBy shouldBeEqualTo "the-current-user"
        ssaved.createdAt.shouldNotBeNull() shouldBeEqualTo loaded.createdAt

        ssaved.lastModifiedBy shouldBeEqualTo "the-current-user"
        ssaved.lastModifiedAt.shouldNotBeNull().`should be in range`(instantRange)
    }

    @Test
    fun `should update auditor for custom auditable order`() = runSuspendTest {
        val order = CustomAuditableOrder("4242")
        order.createdAt.shouldBeNull()
        order.isNew.shouldBeTrue()

        val instantRange = Instant.now().minusSeconds(60)..Instant.now().plusSeconds(60)
        customRepo.save(order).let { actual ->
            log.info { "Actual createdAt=${actual.createdAt}, lastModifiedAt=${actual.modifiedAt}" }

            actual.createdBy shouldBeEqualTo "the-current-user"
            actual.createdAt.shouldNotBeNull().`should be in range`(instantRange)

            actual.modifiedBy shouldBeEqualTo "the-current-user"
            actual.modifiedAt.shouldNotBeNull().`should be in range`(instantRange)
        }

        delay(100)

        val loaded = customRepo.findById("4242")!!
        log.info { "loaded createdAt=${loaded.createdAt}, lastModifiedAt=${loaded.modifiedAt}" }
        loaded.isNew.shouldBeFalse()

        customRepo.save(loaded).let { actual ->
            log.info { "Actual createdAt=${actual.createdAt}, lastModifiedAt=${actual.modifiedAt}" }

            actual.createdBy shouldBeEqualTo "the-current-user"
            actual.createdAt.shouldNotBeNull() shouldBeEqualTo loaded.createdAt

            actual.modifiedBy shouldBeEqualTo "the-current-user"
            actual.modifiedAt.shouldNotBeNull().`should be in range`(instantRange)
        }
    }
}
