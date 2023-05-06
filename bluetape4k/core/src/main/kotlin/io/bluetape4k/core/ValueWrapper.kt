package io.bluetape4k.core

import java.io.Serializable

@JvmInline
value class ValueWrapper(val value: Any?): Serializable {

    inline fun getOrElse(getter: () -> Any?): Any? = value ?: getter()

    fun getOrNull(): Any? = value
}
