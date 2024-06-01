package io.bluetape4k.json.gson

import com.fatboyindustrial.gsonjavatime.Converters
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * [GsonBuilder]를 이용하여 [Gson]을 빌드합니다.
 *
 * ```
 * val gson = gson {
 *     enableComplexMapKeySerialization()
 *     serializeNulls()
 * }
 * ```
 *
 * @param initializer [GsonBuilder]를 이용하여 Gson 설정을 수행합니다.
 * @return [Gson] 인스턴스
 */
inline fun gson(initializer: GsonBuilder.() -> Unit): Gson {
    return GsonBuilder().apply(initializer).create()
}

/**
 * 기본 [Gson] 인스턴스를 생성합니다.
 */
fun createDefaultGson(): Gson = gson {
    enableComplexMapKeySerialization()
    serializeNulls()
    Converters.registerAll(this)
}

/**
 * 기본 [Gson] 인스턴스
 */
val DefaultGson: Gson by lazy { createDefaultGson() }
