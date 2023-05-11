package io.bluetape4k.examples.coroutines.flow

import io.bluetape4k.coroutines.flow.toFastList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

class ChannelFlowExamples {

    companion object: KLogging()

    private data class User(val name: String)

    private interface UserApi {
        suspend fun takePage(pageNumber: Int): Flow<User>
    }

    private class FakeUserApi: UserApi {
        private val users = List(20) { User("User$it") }
        private val pageSize = 3

        override suspend fun takePage(pageNumber: Int): Flow<User> {
            delay(1000)
            return users.asFlow()
                .drop(pageSize * pageNumber)
                .take(pageSize)
        }
    }

    private fun allUsersByFlow(api: UserApi): Flow<User> = flow {
        var page = 0
        do {
            log.trace { "Fetching page $page" }
            val users = api.takePage(page++)
            emitAll(users)
        } while (users.toFastList().isNotEmpty())
    }

    /**
     * 단순 Flow 를 사용하면 요청 시에만 재호출을 수행해서 가져온다
     */
    @Test
    fun `get users by flow`() = runTest {
        val api = FakeUserApi()
        val users = allUsersByFlow(api)

        val user = users
            .firstOrNull {
                log.trace { "Checking $it" }
                delay(1000)
                it.name == "User3"
            }

        user.shouldNotBeNull()
        user.name shouldBeEqualTo "User3"
    }


    private fun allUsersByCannelFlow(api: UserApi): Flow<User> = channelFlow {
        var page = 0
        do {
            log.trace { "Fetching page $page" }
            val users = api.takePage(page++)
            users.collect { send(it) }
        } while (users.toList().isNotEmpty())
    }

    /**
     * ChannelFlow 를 사용하면 On Demand 될 때만 수행하는 것이 아니라, 다음 처리를 미리 수행하게 됩니다.
     */
    @Test
    fun `get users by cannel flow`() = runTest {
        val api = FakeUserApi()
        val users = allUsersByCannelFlow(api)

        val user = users
            .firstOrNull {
                log.trace { "Checking $it" }
                delay(1000)
                it.name == "User3"
            }

        user.shouldNotBeNull()
        user.name shouldBeEqualTo "User3"
    }
}
