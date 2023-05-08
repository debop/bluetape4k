package io.bluetape4k.workshop.application.event.aspect

import org.springframework.context.ApplicationEvent
import kotlin.reflect.KClass

/**
 * @AspectEventEmitter 가 적용된 함수가 실행되면, 결과 값을 [AspectEvent.message] 에 담아서 [AspectEvent] 를 발행한다.
 *
 * @property eventType
 * @property params
 * @constructor Create empty Aspect event emitter
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AspectEventEmitter(
    val eventType: KClass<out ApplicationEvent>,
    val params: String = "",
)
