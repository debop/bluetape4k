package io.bluetape4k.spring.cassandra.async

import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.spring.cassandra.domain.DomainTestConfiguration
import io.bluetape4k.spring.cassandra.domain.model.VersionedEntity
import io.bluetape4k.spring.coroutines.await
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.cassandra.core.AsyncCassandraOperations
import org.springframework.data.cassandra.core.AsyncCassandraTemplate
import org.springframework.data.cassandra.core.query.Query
import org.springframework.data.cassandra.core.selectOne
import org.springframework.data.cassandra.core.truncate
import kotlin.test.assertFailsWith

@SpringBootTest(classes = [DomainTestConfiguration::class])
class AsyncOptimisticLockingTest: AbstractCassandraCoroutineTest("async-optimistic-locking") {

    companion object: KLogging()

    private val operations: AsyncCassandraOperations by lazy { AsyncCassandraTemplate(session) }

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            operations.truncate<VersionedEntity>().await()
        }
    }

    @Test
    fun `context loading`() {
        operations.shouldNotBeNull()
    }

    @Test
    fun `versioned entity 삽입 시 version이 올라간다`() = runSuspendTest {
        val entity = VersionedEntity(42L)

        val saved = operations.insert(entity).await()!!
        val loaded = operations.selectOne<VersionedEntity>(Query.empty()).await()

        saved.version shouldBeEqualTo 1
        loaded.shouldNotBeNull()
        loaded.version shouldBeEqualTo 1
    }

    @Test
    fun `중복된 insert 는 실패한다`() = runSuspendTest {
        operations.insert(VersionedEntity(42L)).await()

        assertFailsWith<OptimisticLockingFailureException> {
            operations.insert(VersionedEntity(42L, 12)).await()
        }
    }

    @Test
    fun `versioned entity를 update하면 version이 올라간다`() = runSuspendTest {
        val entity = VersionedEntity(42L)

        val saved = operations.insert(entity).await()!!
        val updated = operations.update(saved).await()!!
        val loaded = operations.selectOne<VersionedEntity>(Query.empty()).await()

        saved.version shouldBeEqualTo 1
        updated.version shouldBeEqualTo 2
        loaded.shouldNotBeNull()
        loaded.version shouldBeEqualTo 2
    }

    @Test
    fun `outdated entity를 갱신하려면 예외가 발생한다`() = runSuspendTest {
        val entity = VersionedEntity(42L)
        operations.insert(entity).await()!!

        assertFailsWith<OptimisticLockingFailureException> {
            operations.update(entity.copy(version = 42, name = faker.name().name())).await()
        }
    }

    @Test
    fun `versioned entity 삭제하기`() = runSuspendTest {
        val entity = VersionedEntity(42L)
        val saved = operations.insert(entity).await()!!

        operations.delete(saved).await()
        val loaded = operations.selectOne<VersionedEntity>(Query.empty()).await()
        loaded.shouldBeNull()
    }

    @Test
    fun `outdated versioned entity를 삭제하려면 예외가 발생한다`() = runSuspendTest {
        val entity = VersionedEntity(42L)
        val saved = operations.insert(entity).await()!!

        assertFailsWith<OptimisticLockingFailureException> {
            operations.delete(VersionedEntity(42L)).await()
        }

        val loaded = operations.selectOne<VersionedEntity>(Query.empty()).await()
        loaded.shouldNotBeNull() shouldBeEqualTo saved
    }
}
