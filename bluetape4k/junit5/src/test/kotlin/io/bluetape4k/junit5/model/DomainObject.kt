package io.bluetape4k.junit5.model

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

class DomainObject {
    var id: Int = 0
    var name: String? = null
    var value: Long = 0L
    var price: Double = 0.0
    var nestedDomainObject: NestedDomainObject? = null
    var wotsits: List<String>? = null
    val objectLists: MutableList<NestedDomainObject> = mutableListOf()

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE)
    }
}
