package io.bluetape4k.examples.coroutines.builders

import io.bluetape4k.kotlinx.coroutines.context.PropertyCoroutineContext
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CoroutineContextBuilderExamples {

    companion object: KLogging()

    private fun CoroutineScope.logging(msg: String) {
        val name = coroutineContext[CoroutineName]?.name
        val props = coroutineContext[PropertyCoroutineContext]?.properties
        if (name != null) {
            log.debug { "[$name] $msg" }
        } else if (props != null) {
            log.debug { "[$props] $msg" }
        } else {
            log.debug { msg }
        }
    }

    @Test
    fun `부모-자식 간에 CoroutineContext 통해 정보 전달을 한다`() = runTest(CoroutineName("parent")) {
        logging("Started")
        val v1 = async {
            delay(500)
            logging("Running async")
            42
        }

        launch {
            delay(1000)
            logging("Running launch")
        }

        logging("The answer is ${v1.await()}")
        advanceUntilIdle()
    }

    @Test
    fun `자식은 부모의 Context를 재정의 합니다`() = runTest(CoroutineName("parent")) {
        logging("Started")
        val v1 = async(CoroutineName("c1")) {
            delay(500)
            logging("Running async")
            42
        }

        launch(CoroutineName("c2")) {
            delay(1000)
            logging("Running launch")
        }

        logging("The answer is ${v1.await()}")
        advanceUntilIdle()
    }

    @Test
    fun `자식 Context는 부모 Context를 재정의합니다 2`() = runTest(PropertyCoroutineContext(mapOf("key1" to "value1"))) {
        logging("Started")
        val v1 = async(PropertyCoroutineContext(mapOf("key2" to "value2"))) {
            delay(500)
            logging("Running async")
            42
        }

        launch(PropertyCoroutineContext(mapOf("key3" to "value3"))) {
            delay(1000)
            logging("Running launch")
        }

        logging("The answer is ${v1.await()}")
        advanceUntilIdle()
    }
}
