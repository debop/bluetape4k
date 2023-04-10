package io.bluetape4k.junit5.random

import io.github.benas.randombeans.EnhancedRandomBuilder
import io.github.benas.randombeans.api.EnhancedRandom

/**
 * [EnhancedRandom]을 생성해주는 함수
 *
 * @param action
 * @receiver
 * @return
 */
internal fun enhancedRandom(action: EnhancedRandomBuilder.() -> Unit): EnhancedRandom =
    EnhancedRandomBuilder.aNewEnhancedRandomBuilder().apply(action).build()

/**
 * Random 값을 발생시켜주는 기본 Randomizer
 */
internal val DefaultEnhancedRandom: EnhancedRandom by lazy {
    enhancedRandom {
        seed(System.currentTimeMillis())
        objectPoolSize(10_000)
        randomizationDepth(5)
        charset(Charsets.UTF_8)
        stringLengthRange(5, 255)
        collectionSizeRange(5, 20)
        scanClasspathForConcreteTypes(true)
        overrideDefaultInitialization(true)
        ignoreRandomizationErrors(true)
    }
}

inline fun <reified T : Any> EnhancedRandom.newObject(vararg excludeFields: String): T =
    nextObject(T::class.java, *excludeFields)

inline fun <reified T : Any> EnhancedRandom.newList(size: Int, vararg excludeFields: String): List<T> =
    objects(T::class.java, size, *excludeFields).toList()
