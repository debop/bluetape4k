package io.bluetape4k.workshop.webflux.service

import com.fasterxml.jackson.core.JsonProcessingException
import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.logging.info
import io.bluetape4k.utils.idgenerators.uuid.TimebasedUuid
import io.bluetape4k.workshop.webflux.model.Event
import io.bluetape4k.workshop.webflux.model.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.math.BigDecimal
import java.math.MathContext
import java.time.Duration
import java.time.Instant
import kotlin.random.Random

@Service
class QuoteGenerator {

    companion object: KLogging() {
        private val mapper = Jackson.defaultJsonMapper
        private val MATH_CONTEXT = MathContext(2)
    }

    private val prices = fastListOf(
        Quote("APPL", BigDecimal(82.26, MATH_CONTEXT)),
        Quote("TSLA", BigDecimal(63.74, MATH_CONTEXT)),
        Quote("GOOG", BigDecimal(847.24, MATH_CONTEXT)),
        Quote("MSFT", BigDecimal(165.11, MATH_CONTEXT)),
        Quote("AMZN", BigDecimal(35.71, MATH_CONTEXT)),
        Quote("NFLX", BigDecimal(84.29, MATH_CONTEXT)),
        Quote("INTC", BigDecimal(20.21, MATH_CONTEXT)),
    )

    fun fetchQuoteStringAsFlux(period: Duration): Flux<String> {
        return Flux.interval(period)
            .map { prices.random() }
            .map { mapper.writeValueAsString(it) }
            .log("fetchQuoteStringStream")
    }

    fun fetchQuoteStringFlow(period: Duration): Flow<String> = flow {
        while (currentCoroutineContext().isActive) {
            delay(period.toMillis())
            val quotes = generateStringQuotes(period.toMillis())
            emit(quotes)
            log.info { "emit quote. $quotes" }
        }
    }.conflate()   // conflate는 onBackpressureDrop() 과 같이 적체되면 무시해버린다.

    private fun generateStringQuotes(interval: Long): String {
        return try {
            mapper.writeValueAsString(createEvent(interval))
        } catch (e: JsonProcessingException) {
            log.error(e) { "Fail to json serialize." }
            "{}"
        }
    }

    fun fetchQuoteAsFlow(period: Duration): Flow<Quote> = flow {
        withTimeoutOrNull(period.toMillis()) {
            while (currentCoroutineContext().isActive) {
                val quotes = generateQuotes(period.toMillis())
                emitAll(quotes.asFlow().onEach { log.info { "emit quote. $it" } })
                yield()
            }
        }
    }
        .flowOn(Dispatchers.IO)
        .conflate()  // conflate는 onBackpressureDrop() 과 같이 적체되면 무시해버린다.

    fun getQuotes(): Flow<Event> {
        // conflate는 onBackpressureDrop() 과 같이 적체되면 무시해버린다.
        return flowOf(createEvent(0)).conflate().flowOn(Dispatchers.IO)
    }

    private fun createEvent(interval: Long): Event {
        val traceId = TimebasedUuid.nextBase62String()
        return Event(traceId, generateQuotes(interval))
    }

    private fun generateQuotes(interval: Long): List<Quote> {
        val instant = Instant.now()

        return prices.collect { baseQuote ->
            val priceChanges = baseQuote.price * BigDecimal(0.05 * Random.nextDouble(), MATH_CONTEXT)
            baseQuote.copy(price = baseQuote.price + priceChanges, instant = instant)
        }
    }
}
