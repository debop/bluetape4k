package io.bluetape4k.core

import java.io.Serializable

interface ValueObject: Serializable

/**
 * [ValueObject]의 최상위 추상화 클래스입니다.
 */
abstract class AbstractValueObject: ValueObject {
    /** Class의 고유성을 표현하는 속성들이 같은지 비교한다 */
    protected abstract fun equalProperties(other: Any): Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        return equalProperties(other)
    }

    override fun hashCode(): Int = System.identityHashCode(this)

    override fun toString(): String = buildStringHelper().toString()

    open fun toString(limit: Int): String = buildStringHelper().toString(limit)

    protected open fun buildStringHelper(): ToStringBuilder = ToStringBuilder(this)

}
