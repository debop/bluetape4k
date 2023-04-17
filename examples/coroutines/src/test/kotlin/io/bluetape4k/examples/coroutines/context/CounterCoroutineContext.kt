package io.bluetape4k.examples.coroutines.context

import kotlinx.atomicfu.atomic
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Counter 를 가지는 [CoroutineContext]
 *
 * @property name coroutine context name
 */
class CounterCoroutineContext(private val name: String): AbstractCoroutineContextElement(Key) {

    companion object Key: CoroutineContext.Key<CounterCoroutineContext>

    private val nextNumber = atomic(0L)

    val number: Long get() = nextNumber.value

    fun printNextCount() {
        println(this)
        nextNumber.incrementAndGet()
    }

    override fun toString(): String {
        return "CounterCoroutineContext(name='$name', number=$number)"
    }
}

/**
 * Current CoroutineScope 에서 [CounterCoroutineContext]를 찾아서 [CounterCoroutineContext.number]를 출력합니다.
 */
suspend fun printNextCount() {
    coroutineContext[CounterCoroutineContext]?.printNextCount()
}
