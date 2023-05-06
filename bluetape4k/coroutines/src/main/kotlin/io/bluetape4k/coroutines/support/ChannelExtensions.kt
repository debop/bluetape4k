package io.bluetape4k.coroutines.support

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.time.Duration
import kotlin.coroutines.CoroutineContext

private val log = KotlinLogging.logger { }

/**
 * 연속해서 중복되는 요소를 제거하도록 합니다.
 *
 * @param T
 * @param context
 * @return
 */
suspend fun <E> ReceiveChannel<E>.distinctUntilChanged(
    context: CoroutineContext = Dispatchers.Default,
): ReceiveChannel<E> = coroutineScope {
    val self = this@distinctUntilChanged
    produce(context, Channel.BUFFERED) {
        val producer = this
        var prev: E? = null

        self.consumeEach { received ->
            log.trace { "Received: $received" }
            if (received != prev) {
                log.trace { "Send: $received" }
                producer.send(received)
                prev = received
            }
        }
        producer.close()
    }
}

/**
 * 연속해서 중복되는 요소를 제거하도록 합니다.
 *
 * @param T
 * @param context
 * @param equalOperator 요소들을 비교해서 같은지 판단하도록 한다 (두 요소가 같으면 true를 반환)
 * @return
 */
suspend fun <E> ReceiveChannel<E>.distinctUntilChanged(
    context: CoroutineContext = Dispatchers.Default,
    equalOperator: (E, E) -> Boolean,
): ReceiveChannel<E> = coroutineScope {
    val self = this@distinctUntilChanged
    produce(context, Channel.BUFFERED) {
        val producer = this
        var prev: E = self.receive()
        producer.send(prev)

        self.consumeEach { received ->
            log.trace { "Received: $received" }
            if (!equalOperator(received, prev)) {
                log.debug { "Send: $received" }
                producer.send(received)
                prev = received
            }
        }
        producer.close()
    }
}

/**
 * 수신 받은 요소들을 이용하여 [accumulator]를 통해 reduce 한 값을 다시 send 합니다.
 *
 * @param E
 * @param context
 * @param accumulator
 * @return
 */
suspend fun <E> ReceiveChannel<E>.reduce(
    context: CoroutineContext = Dispatchers.Default,
    accumulator: (acc: E, item: E) -> E,
): ReceiveChannel<E> = coroutineScope {
    produce(context, Channel.BUFFERED) {
        var acc = receive()
        consumeEach { received ->
            acc = accumulator(acc, received)
        }
        send(acc)
    }
}

/**
 * 수신 받은 요소들을 이용하여 [accumulator]를 통해 reduce 한 값을 다시 send 합니다.
 *
 * @param E
 * @param context
 * @param accumulator
 * @return
 */
suspend fun <E> ReceiveChannel<E>.reduce(
    initValue: E,
    context: CoroutineContext = Dispatchers.Default,
    accumulator: (acc: E, item: E) -> E,
): ReceiveChannel<E> = coroutineScope {
    produce(context, Channel.BUFFERED) {
        var acc = initValue
        consumeEach { received ->
            acc = accumulator(acc, received)
        }
        send(acc)
    }
}

/**
 * 두 개의 [ReceiveChannel] 에서 수신 받은 것을 번갈아가며 produce 합니다.
 *
 * @param E
 * @param other
 * @param context
 * @return
 */
suspend fun <E> ReceiveChannel<E>.concatWith(
    other: ReceiveChannel<E>,
    context: CoroutineContext = Dispatchers.Default,
): ReceiveChannel<E> = coroutineScope {
    produce(context, Channel.BUFFERED) {
        consumeEach { send(it) }
        other.consumeEach { send(it) }
    }
}

/**
 * 두 개의 [ReceiveChannel] 에서 수신 받은 것을 번갈아가며 produce 합니다.
 *
 * @param E
 * @param context
 * @param first
 * @param second
 * @return
 */
suspend fun <E> concat(
    first: ReceiveChannel<E>,
    second: ReceiveChannel<E>,
    context: CoroutineContext = Dispatchers.Default,
): ReceiveChannel<E> = first.concatWith(second, context)

/**
 * relay 할 때, [waitDuration] 만큼 지연 시키고, 가장 최신의 수신 요소를 relay 합니다.
 *
 * @param E
 * @param waitDuration
 * @param context
 * @return
 */
suspend fun <E> ReceiveChannel<E>.debounce(
    waitDuration: Duration,
    context: CoroutineContext = Dispatchers.Default,
): ReceiveChannel<E> = coroutineScope {
    val self = this@debounce
    require(!waitDuration.isNegative) { "waitDuration must be zero or positive value." }
    produce(context, Channel.BUFFERED) {
        val producer = this@produce
        val waitMillis = waitDuration.toMillis()
        var nextTime = 0L
        self.consumeEach { received ->
            val currentTime = System.currentTimeMillis()
            if (currentTime < nextTime) {
                // 지연시키기
                delay(minOf(nextTime - currentTime, waitMillis))
                var mostRecent = received
                // channel에 요소가 있다면 가장 최신의 요소를 얻기 위해 계속 수신합니다. (중간 요소들은 모두 무시됩니다)
                while (!self.isEmpty && !self.isClosedForReceive) {
                    mostRecent = self.receive()
                }
                nextTime += waitMillis
                producer.send(mostRecent)
            } else {
                nextTime = currentTime + waitMillis
                producer.send(received)
            }
        }
        producer.close()
    }
}
