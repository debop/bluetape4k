package io.bluetape4k.coroutines.chains

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A not-necessary-Markov chain of some type
 *
 * @param T
 * @constructor Create empty Chain
 */
interface Chain<out T>: Flow<T> {
    /**
     * 다음 값을 생성하고, 필요한 경우 상태를 변경한다.
     */
    suspend fun next(): T

    /**
     * 현 상태의 체인을 복사한다. 결과 체인을 소비하면 초기 체인에 영향을 주지 않는다.
     */
    suspend fun fork(): Chain<T>

    override suspend fun collect(collector: FlowCollector<T>): Unit =
        flow { while (true) emit(next()) }.collect(collector)
}

fun <T> Iterator<T>.asChain(): Chain<T> = SimpleChain { next() }
fun <T> Sequence<T>.asChain(): Chain<T> = iterator().asChain()
fun <T> Iterable<T>.asChain(): Chain<T> = iterator().asChain()

class SimpleChain<out T>(private val generator: () -> T): Chain<T> {
    override suspend fun next(): T = generator.invoke()
    override suspend fun fork(): Chain<T> = this
}

/**
 *  A stateless Markov chain
 *
 * @property seed
 * @property gen
 * @constructor Create empty Markov chain
 */
class MarkovChain<out T: Any>(
    private val seed: () -> T,
    private val gen: (T) -> T,
): Chain<T> {

    private val mutex: Mutex = Mutex()
    private var value: T? = null

    fun value(): T? = value

    override suspend fun next(): T = mutex.withLock {
        val newValue = gen(value ?: seed())
        value = newValue
        newValue
    }

    override suspend fun fork(): Chain<T> = MarkovChain(seed = { value ?: seed() }, gen = gen)
}

/**
 * 변경 가능한 상태를 가진 체인입니다. 체인 외부에서 상태를 변경해서는 안 됩니다. 복수의 Chain들이 상태를 공유해서는 안 됩니다.
 *
 * @param S the state of the chain.
 * @param forkState the function to copy current state without modifying it.
 */
class StatefulChain<S, out T>(
    private val state: S,
    private val seed: S.() -> T,
    private val forkState: ((S) -> S),
    private val gen: S.(T) -> T,
): Chain<T> {

    private val mutex = Mutex()
    private var value: T? = null

    fun value(): T? = value

    override suspend fun next(): T = mutex.withLock {
        val newValue = state.gen(value ?: state.seed())
        value = newValue
        newValue
    }

    override suspend fun fork(): Chain<T> {
        return StatefulChain(forkState(state), seed, forkState, gen)
    }
}

/**
 * A chain that always returns the same value
 */
class ConstantChain<out T>(val value: T): Chain<T> {
    override suspend fun next(): T = value
    override suspend fun fork(): Chain<T> = this
}

/**
 * Map the chain result using suspended transformation. Initial chain result can no longer be safely consumed
 * since mapped chain consumes tokens. Accepts regular transformation function.
 */
fun <T, R> Chain<T>.map(transform: suspend (T) -> R): Chain<R> {
    return object: Chain<R> {
        override suspend fun next(): R = transform(this@map.next())
        override suspend fun fork(): Chain<R> = this@map.fork().map(transform)
    }
}

/**
 * [predicate] must be a pure function or at least not use external random variables, otherwise fork could be broken
 */
fun <T> Chain<T>.filter(predicate: suspend (T) -> Boolean): Chain<T> = object: Chain<T> {
    override suspend fun next(): T {
        var next: T

        do {
            next = this@filter.next()
        } while (!predicate(next))

        return next
    }

    override suspend fun fork(): Chain<T> = this@filter.fork().filter(predicate)
}

fun <T, R> Chain<T>.combine(mapper: suspend (Chain<T>) -> R): Chain<R> = object: Chain<R> {
    override suspend fun next(): R = mapper(this@combine)
    override suspend fun fork(): Chain<R> = this@combine.fork().combine(mapper)
}

fun <T, S, R> Chain<T>.combineWithState(
    state: S,
    stateFork: (S) -> S,
    mapper: suspend S.(Chain<T>) -> R,
): Chain<R> = object: Chain<R> {
    override suspend fun next(): R = state.mapper(this@combineWithState)
    override suspend fun fork(): Chain<R> =
        this@combineWithState.fork().combineWithState(stateFork(state), stateFork, mapper)
}

/**
 * Zip two chains together using given transformation
 */
fun <T, U, R> Chain<T>.zip(other: Chain<U>, zipper: suspend (T, U) -> R): Chain<R> = object: Chain<R> {
    override suspend fun next(): R = zipper(this@zip.next(), other.next())
    override suspend fun fork(): Chain<R> = this@zip.fork().zip(other.fork(), zipper)
}
