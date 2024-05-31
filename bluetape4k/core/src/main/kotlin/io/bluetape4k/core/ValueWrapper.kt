package io.bluetape4k.core

import java.io.Serializable

/**
 * [value] 를 감싸서, [Serializable]이 되도록 합니다.
 *
 * @property value
 */
@JvmInline
value class ValueWrapper(val value: Any?): Serializable {

    inline fun getOrElse(getter: () -> Any?): Any? = value ?: getter()

    fun getOrNull(): Any? = value

    inline fun <reified T> getAs(): T? = (value as? T)
}
