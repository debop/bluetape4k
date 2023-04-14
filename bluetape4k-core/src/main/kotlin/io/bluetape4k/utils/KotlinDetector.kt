package io.bluetape4k.utils

import io.bluetape4k.logging.KLogging
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberExtensionFunctions
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.kotlinFunction

@Suppress("UNCHECKED_CAST")
object KotlinDetector : KLogging() {

    val kotlinMetadata: Class<out Annotation>? by lazy {
        try {
            Class.forName("kotlin.Metadata", false, KotlinDetector::class.java.classLoader)
        } catch (e: ClassNotFoundException) {
            null
        } as? Class<out Annotation>
    }

    /**
     * 현 프로세스에서 Kotlin을 사용할 수 있는지 알려준다
     */
    fun isKotlinPresent(): Boolean = kotlinMetadata != null

    /**
     * 지정한 수형이 Kotlin 으로 정의된 수형인가 판단합니다.
     *
     * @param clazz Class<*> 검사할 수형
     * @return Boolean 수형이 Kotlin으로 정의되었다면 true, 아니면 False를 반환
     */
    fun isKotlinType(clazz: Class<*>): Boolean =
        isKotlinPresent() && clazz.getDeclaredAnnotation(kotlinMetadata) != null
}

/**
 * 현 수형이 Kotlin 으로 정의된 수형인가 판단합니다.
 * @receiver Class<*> 검사할 수형
 * @return Boolean 수형이 Kotlin으로 정의되었다면 true, 아니면 False를 반환
 */
fun Class<*>.isKotlinType(): Boolean = KotlinDetector.isKotlinType(this)


/**
 * 메소드가 `suspend` 메소드인지 판단합니다.
 *
 * @param methodName 메소드 명
 * @return suspend 함수인지 여부
 */
fun KClass<*>.isSuspendableFunction(methodName: String): Boolean =
    memberFunctions.firstOrNull { it.name == methodName } != null ||
    memberExtensionFunctions.firstOrNull { it.name == methodName } != null

/**
 * 현 클래스의 `suspend` 메소드들의 컬렉션을 조회합니다.
 *
 * @return suspend 함수 컬렉션
 */
fun KClass<*>.getSuspendableFunctions(): List<KFunction<*>> =
    memberFunctions.filter { it.isSuspend } +
    memberExtensionFunctions.filter { it.isSuspend }

/**
 * Method가 `suspend` 메소드인지 판단합니다.
 *
 * @return suspend 메소드이면 true, 아니면 false
 */
fun Method.isSuspendableFunction(): Boolean =
    this.kotlinFunction?.run { isSuspend } ?: false
