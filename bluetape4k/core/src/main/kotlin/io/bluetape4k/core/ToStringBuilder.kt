package io.bluetape4k.core

import java.io.Serializable

/**
 * Business Entity의 toString()을 손쉽게 설정할 수 있게 해주는 Builder 입니다.
 *
 * @see AbstractValueObject
 */
class ToStringBuilder private constructor(private val className: String): Serializable {

    companion object {
        operator fun invoke(className: String): ToStringBuilder {
            className.assertNotBlank("className")
            return ToStringBuilder(className)
        }

        operator fun invoke(obj: Any): ToStringBuilder {
            return invoke(obj.javaClass.simpleName)
        }
    }

    private val map = LinkedHashMap<String, Any?>()
    private lateinit var cachedToString: String

    private fun toStringValue(limit: Int): String {
        if (!::cachedToString.isInitialized) {
            val props = map.entries.joinToString(separator = ",", limit = limit) {
                "${it.key}=${it.value.asString()}"
            }
            cachedToString = "$className($props)"
        }
        return cachedToString
    }

    private fun Any?.asString(): String = this?.toString() ?: "<null>"

    fun add(name: String, value: Any?): ToStringBuilder = apply {
        map[name] = value?.asString()
    }

    fun toString(limit: Int): String = toStringValue(limit)

    override fun toString(): String = toStringValue(-1)

}
