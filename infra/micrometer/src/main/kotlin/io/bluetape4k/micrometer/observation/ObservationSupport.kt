package io.bluetape4k.micrometer.observation

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry

/**
 * [name]을 가진 Observation을 생성하고, [block]을 실행합니다.
 *
 * @param T
 * @param name Observation의 이름
 * @param registry [ObservationRegistry]
 * @param block 측정할 코드 블록
 * @receiver
 * @return
 */
@Suppress("UNCHECKED_CAST")
fun <T> withObservation(
    name: String,
    registry: ObservationRegistry,
    block: () -> T,
): T {
    return Observation.createNotStarted(name, registry).observe { block.invoke() } as T
}

fun ObservationRegistry.start(
    name: String,
    contextualName: String = name,
): Observation {
    return createNotStarted(name, contextualName).start()
}

fun ObservationRegistry.createNotStarted(
    name: String,
    contextualName: String = name,
): Observation {
    return Observation.createNotStarted(
        name,
        {
            Observation.Context().apply {
                put("name", name)
                put("contextualName", contextualName)
            }
        },
        this
    )
}
