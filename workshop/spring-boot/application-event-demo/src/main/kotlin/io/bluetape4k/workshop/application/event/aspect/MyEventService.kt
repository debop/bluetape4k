package io.bluetape4k.workshop.application.event.aspect

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.stereotype.Service
import java.io.Serializable

@Service
class MyEventService {

    companion object: KLogging()

    /**
     * 함수 실행의 결과 값인 [OperationParams]에서 id 값을 추출해서, [MyAspectParams] 를 만들고,
     * [AspectEvent.message]에 담아서 [AspectEvent] 를 발행한다.
     */
    @AspectEventEmitter(
        eventType = AspectEvent::class,
        params = """#{ T(io.bluetape4k.workshop.application.event.aspect.MyAspectParams).create(id) }"""
    )
    fun someOperation(params: OperationParams): OperationParams {
        val message = "Some operations is executed. $params"
        log.debug { message }
        return params
    }
}

data class OperationParams(
    val id: String,
    val type: String,
): Serializable

data class MyAspectParams(
    val message: String?,
): Serializable {
    companion object {
        @JvmStatic
        fun create(message: String?): MyAspectParams {
            return MyAspectParams(message)
        }
    }
}
