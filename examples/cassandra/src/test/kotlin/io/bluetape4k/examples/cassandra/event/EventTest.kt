package io.bluetape4k.examples.cassandra.event

import io.bluetape4k.coroutines.flow.eclipse.toFastList
import io.bluetape4k.examples.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.cassandra.selectForFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.CassandraOperations
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.query.Query
import org.springframework.data.cassandra.core.select
import org.springframework.data.cassandra.core.stream
import org.springframework.data.cassandra.core.truncate

@SpringBootTest(classes = [EventTestConfiguration::class])
class EventTest(
    @Autowired private val operations: CassandraOperations,
    @Autowired private val reactiveOperations: ReactiveCassandraOperations,
): AbstractCassandraCoroutineTest("event") {

    companion object: KLogging()

    @BeforeEach
    fun beforeEach() {
        operations.truncate<User>()
    }

    @Test
    fun `Stream 방식으로 데이터 로딩하기`() {
        insertEntities()

        val userStream = operations.stream<User>(Query.empty())
        userStream.forEach { println(it) }
    }

    @Test
    fun `List 로 데이터 로딩하기`() {
        insertEntities()

        val users = operations.select<User>(Query.empty())
        users.size shouldBeEqualTo 3
        users.forEach { println(it) }
    }

    @Test
    fun `Flow 로 데이터 로딩하기`() = runSuspendWithIO {
        withContext(Dispatchers.IO) {
            insertEntities()
        }

        val userFlow = reactiveOperations.selectForFlow<User>(Query.empty()).toFastList()
        userFlow.size shouldBeEqualTo 3
    }

    private fun insertEntities() {
        val walter = User(1, "Walter", "White")
        val skyler = User(2, "Skyler", "White")
        val jesse = User(3, "Jesse Pinkman", "Jesse Pinkman")

        operations.insert(walter)
        operations.insert(skyler)
        operations.insert(jesse)
    }
}
