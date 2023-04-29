package io.bluetape4k.spring.util

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.logging.KotlinLogging
import java.util.UUID
import org.springframework.util.StopWatch

private val log = KotlinLogging.logger { }

/**
 * 지정한 body를 실행할 때, [StopWatch]를 이용하여 실행시간을 측정합니다.
 *
 * ```kotlin
 * val sw = withStopWatch("async") {
 *     delay(100)
 * }
 * println(sw.prettyPrint())
 * ```
 *
 * @param id StopWatch의 Id 값
 * @param body suspended 함수
 * @return StopWatch 인스턴스
 */
@JvmOverloads
inline fun withStopWatch(id: String = UUID.randomUUID().encodeBase62(), body: () -> Unit): StopWatch {
    return StopWatch(id).apply {
        start()
        try {
            body()
        } finally {
            stop()
        }
    }
}

inline fun <T> StopWatch.task(name: String = UUID.randomUUID().encodeBase62(), body: () -> T): T {
    check(!isRunning) { "StopWatch already started, please stop at first." }
    return try {
        start(name)
        body()
    } finally {
        stop()
    }
}
