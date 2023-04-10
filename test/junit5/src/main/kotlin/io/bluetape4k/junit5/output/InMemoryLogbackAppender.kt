package io.bluetape4k.junit5.output

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import io.bluetape4k.logging.KLogging
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KClass

/**
 * 로그 메시지를 메모리에 캡쳐하기 위한 Logback Appender입니다.
 *
 * NOTE: 단 parallel 테스트 시에는 제대로 Logger를 casting 할 수 없습니다.
 * HINT : http://www.slf4j.org/codes.html#substituteLogger
 */
class InMemoryLogbackAppender private constructor(name: String) : AppenderBase<ILoggingEvent>() {

    companion object : KLogging() {
        operator fun invoke(name: String = "root"): InMemoryLogbackAppender = InMemoryLogbackAppender(name)
        operator fun invoke(clazz: Class<*>): InMemoryLogbackAppender = invoke(clazz.name)
        operator fun invoke(kclazz: KClass<*>): InMemoryLogbackAppender = invoke(kclazz.qualifiedName!!)
    }

    private val logger by lazy {
        do {
            Thread.sleep(1)
            val logger = LoggerFactory.getLogger(name)
        } while (logger !is ch.qos.logback.classic.Logger)

        LoggerFactory.getLogger(name) as ch.qos.logback.classic.Logger
    }

    private val events = LinkedList<ILoggingEvent>()

    val size: Int get() = events.size
    val lastMessage: String? get() = events.lastOrNull()?.message
    val messages: List<String> get() = events.map { it.message }

    init {
        start()
        logger.addAppender(this)
    }

    override fun append(eventObject: ILoggingEvent?) {
        eventObject?.run { events.add(this) }
    }

    override fun stop() {
        logger.detachAppender(this)
        clear()
        super.stop()
    }

    fun clear() {
        events.clear()
    }
}