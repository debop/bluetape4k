package io.bluetape4k.aws.dynamodb.model

import io.bluetape4k.aws.core.toSdkBytes
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.io.InputStream
import java.nio.ByteBuffer

inline fun AttributeValue(initializer: AttributeValue.Builder.() -> Unit): AttributeValue {
    return AttributeValue.builder().apply(initializer).build()
}

fun ByteArray.toAttributeValue(): AttributeValue = AttributeValues.binaryValue(this.toSdkBytes())
fun ByteBuffer.toAttributeValue(): AttributeValue = AttributeValues.binaryValue(this.toSdkBytes())
fun String.toAttributeValue(): AttributeValue = AttributeValues.stringValue(this)
fun Number.toAttributeValue(): AttributeValue = AttributeValues.numberValue(this)

fun Boolean.toAttributeValue(): AttributeValue = AttributeValue { bool(this@toAttributeValue) }
fun Boolean.toNullAttributeValue(): AttributeValue = AttributeValue { nul(this@toNullAttributeValue) }

fun Iterable<*>.toAttributeValue(): AttributeValue = AttributeValue {
    l(this@toAttributeValue.map { it.toAttributeValue() })
}

fun Map<*, *>.toAttributeValue(): AttributeValue = AttributeValue {
    m(this@toAttributeValue.entries.associate { it.key as String to it.value.toAttributeValue() })
}

fun InputStream.toAttributeValue(): AttributeValue = AttributeValue { b(toSdkBytes()) }

fun <T> T.toAttributeValue(): AttributeValue = when (this) {
    null           -> AttributeValues.nullAttributeValue()
    is ByteArray   -> toAttributeValue()
    is ByteBuffer  -> toAttributeValue()
    is String      -> toAttributeValue()
    is Number      -> toAttributeValue()
    is Boolean     -> toAttributeValue()
    is Iterable<*> -> toAttributeValue()
    is Map<*, *>   -> toAttributeValue()
    is InputStream -> toAttributeValue()
    else           -> AttributeValue { s(this@toAttributeValue.toString()) }
}
