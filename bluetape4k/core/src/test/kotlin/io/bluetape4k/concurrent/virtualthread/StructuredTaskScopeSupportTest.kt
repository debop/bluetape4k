package io.bluetape4k.concurrent.virtualthread

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutionException
import kotlin.test.assertFailsWith

class StructuredTaskScopeSupportTest {

    @Test
    fun `첫번째 완료된 작업의 결과를 얻는다`() {
        val result = structuredTaskScopeFirst<String> { scope ->

            scope.fork {
                Thread.sleep(100)
                "result1"
            }
            scope.fork {
                Thread.sleep(200)
                "result2"
            }

            scope.join()

            // 작업들이 완료되지 전에 예외가 발생한다면, 예외를 던진다.
            scope.result { IllegalStateException(it) }
        } // 먼저 완료되는 작업의 결과를 반환한다.
        result shouldBeEqualTo "result1"
    }

    @Test
    fun `첫번째 성공한 결과를 반환한다`() {
        val result = structuredTaskScopeFirst<String> { scope ->

            scope.fork {
                Thread.sleep(100)
                throw RuntimeException("Boom!")
            }
            scope.fork {
                Thread.sleep(200)
                "result2"
            }

            // 작업들이 완료되지 전에 예외가 발생한다면, 예외를 던진다.
            scope.join().result { IllegalStateException(it) }
        } // 먼저 완료되는 작업의 결과를 반환한다.

        result shouldBeEqualTo "result2"
    }

    @Test
    fun `모든 작업이 실패한다면, 첫번째 예외를 반환한다`() {
        assertFailsWith<IllegalStateException> {
            structuredTaskScopeFirst<String> { scope ->
                scope.fork {
                    Thread.sleep(100)
                    throw RuntimeException("Boom 1")
                }
                scope.fork {
                    Thread.sleep(200)
                    throw IllegalArgumentException("Boom 2")
                }

                // 작업들이 완료되지 전에 예외가 발생한다면, 예외를 던진다.
                scope.join().result { IllegalStateException(it) }
            }
        }.cause shouldBeInstanceOf RuntimeException::class
    }

    @Test
    fun `모든 SubTask 들이 완료될 때 결과를 반환한다`() {
        val results = structuredTaskScopeAll { scope ->
            val result1 = scope.fork {
                Thread.sleep(100)
                "result1"
            }
            val result2 = scope.fork {
                Thread.sleep(200)
                "result2"
            }

            scope.join().throwIfFailed()

            listOf(result1.get(), result2.get())
        }

        results shouldBeEqualTo listOf("result1", "result2")
    }

    @Test
    fun `Subtask에서 예외가 발생하면 예외를 던진다`() {
        assertFailsWith<ExecutionException> {
            structuredTaskScopeAll { scope ->
                val result1 = scope.fork {
                    Thread.sleep(100)
                    "result1"
                }
                val result2 = scope.fork {
                    Thread.sleep(200)
                    throw RuntimeException("Boom!")
                }

                scope.join().throwIfFailed()

                listOf(result1.get(), result2.get())
            }
        }.cause shouldBeInstanceOf RuntimeException::class
    }
}
