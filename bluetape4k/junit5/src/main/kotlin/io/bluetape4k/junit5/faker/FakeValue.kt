package io.bluetape4k.junit5.faker

import kotlin.reflect.KClass

/**
 * FakeValue
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.ANNOTATION_CLASS
)
@MustBeDocumented
annotation class FakeValue(
    val provider: String = FakeValueProvider.Name.FullName,
    val size: Int = 1,
    val type: KClass<*> = Any::class,
)
