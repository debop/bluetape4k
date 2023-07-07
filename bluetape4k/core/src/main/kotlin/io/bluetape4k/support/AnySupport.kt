package io.bluetape4k.support

import java.lang.reflect.Method
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.reflect.KClass

/**
 * var 로 선언된 필드 중 non null 수형에 대해 초기화 값을 지정하고자 할 때 사용합니다.
 * 또한 `@Autowired`, `@Inject` val 수형에 사용하기 좋다.
 *
 * ```
 * @Inject val x: Repository = uninitialized()
 * ```
 */
@Suppress("UNCHECKED_CAST")
fun <T> uninitialized(): T = null as T

/**
 * 인스턴스를 [Optional]로 변환합니다.
 */
fun <T: Any> T?.toOptional(): Optional<T> = Optional.ofNullable(this)

fun Any.unwrapOptional(): Any? {
    if (this is Optional<*>) {
        if (!this.isPresent) {
            return null
        }
        val result = this.get()
        check(result !is Optional<*>) { "Multi-level Optional usage not allowed." }
        return result
    }
    return this
}

val Any.isArray: Boolean get() = this.javaClass.isArray

/**
 * 객체들을 조합하여 hash 값을 계산합니다.
 */
fun hashOf(vararg values: Any?): Int = Objects.hash(*values)

/**
 * 두 객체가 같은지 판단합니다. (둘 다 null이면 true를 반환합니다)
 */
fun areEquals(a: Any?, b: Any?): Boolean =
    (a == null && b == null) || (a != null && a == b)

/**
 * 두 객체가 모두 null인 경우는 false를 반환하고, array 인 경우에는 array 요소까지 비교합니다.
 */
fun areEqualsSafe(a: Any?, b: Any?): Boolean {
    if (a === b)
        return true

    if (a == null || b == null)
        return false

    if (a == b)
        return true

    if (a.javaClass.isArray && b.javaClass.isArray) {
        return arrayEquals(a, b)
    }

    return false
}

/**
 * 두 Object 가 같은 것인가 검사한다. Array인 경우도 검사할 수 있습니다.
 */
fun arrayEquals(a: Any, b: Any): Boolean {
    if (a is Array<*> && b is Array<*>) {
        return a.contentEquals(b)
    }
    if (a is BooleanArray && b is BooleanArray) {
        return a.contentEquals(b)
    }
    if (a is ByteArray && b is ByteArray) {
        return a.contentEquals(b)
    }
    if (a is CharArray && b is CharArray) {
        return a.contentEquals(b)
    }
    if (a is DoubleArray && b is DoubleArray) {
        return a.contentEquals(b)
    }
    if (a is FloatArray && b is FloatArray) {
        return a.contentEquals(b)
    }
    if (a is IntArray && b is IntArray) {
        return a.contentEquals(b)
    }
    if (a is LongArray && b is LongArray) {
        return a.contentEquals(b)
    }
    if (a is ShortArray && b is ShortArray) {
        return a.contentEquals(b)
    }

    return false
}

/**
 * 컬렉션의 모든 요소가 not null 인 경우에만 [block]을 수행합니다.
 */
infix fun <T: Any, R: Any> Collection<T?>.whenAllNotNull(block: (Collection<T>) -> R) {
    if (this.all { it != null }) {
        block(this.filterNotNull())
    }
}

/**
 * 컬렉션의 요소중 하나라도 null이 아니라면 [block]을 수행합니다.
 */
infix fun <T: Any, R: Any> Collection<T?>.whenAnyNotNull(block: (Collection<T>) -> R) {
    if (this.any { it != null }) {
        block(this.filterNotNull())
    }
}

fun <T: Any> T?.hashCodeSafe(): Int {
    if (this == null) {
        return 0
    }
    if (this.isArray) {
        when (this) {
            is Array<*>     -> Arrays.hashCode(this)
            is BooleanArray -> Arrays.hashCode(this)
            is ByteArray    -> Arrays.hashCode(this)
            is CharArray    -> Arrays.hashCode(this)
            is DoubleArray  -> Arrays.hashCode(this)
            is FloatArray   -> Arrays.hashCode(this)
            is IntArray     -> Arrays.hashCode(this)
            is LongArray    -> Arrays.hashCode(this)
            is ShortArray   -> Arrays.hashCode(this)
            else            -> Objects.hash(this)
        }
    }
    return this.hashCode()
}

fun Any?.identityToString(): String = when (this) {
    null -> EMPTY_STRING
    else -> javaClass.name + "@" + this.identityHexString()
}

fun Any.identityHexString(): String = Integer.toHexString(System.identityHashCode(this))

fun Any?.toStr(): String = try {
    when (this) {
        null                                             -> "null"
        is BooleanArray                                  -> this.contentToString()
        is ByteArray                                     -> this.contentToString()
        is CharArray                                     -> this.contentToString()
        is ShortArray                                    -> this.contentToString()
        is IntArray                                      -> this.contentToString()
        is LongArray                                     -> this.contentToString()
        is FloatArray                                    -> this.contentToString()
        is DoubleArray                                   -> this.contentToString()
        is Array<*>                                      -> this.contentDeepToString()
        Void.TYPE.kotlin                                 -> "void"
        kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED -> "SUSPEND_MARKER"
        is Continuation<*>                               -> "continuation {}"
        is KClass<*>                                     -> this.simpleName ?: "<null name class>"
        is Method                                        -> name + "(" + parameterTypes.joinToString { it.simpleName } + ")"
        is Function<*>                                   -> "lambda {}"
        else                                             -> toString()
    }
} catch (thr: Throwable) {
    "<error \"$thr\">"
}
