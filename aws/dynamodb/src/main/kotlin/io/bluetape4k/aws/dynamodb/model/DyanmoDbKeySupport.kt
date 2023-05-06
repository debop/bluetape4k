package io.bluetape4k.aws.dynamodb.model

import io.bluetape4k.aws.core.toSdkBytes
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

inline fun dynamoDbKey(initializer: Key.Builder.() -> Unit): Key {
    return Key.builder().apply(initializer).build()
}

fun dynamoDbKeyOf(partitionKey: AttributeValue, sortValue: AttributeValue? = null): Key = dynamoDbKey {
    partitionValue(partitionKey)
    sortValue(sortValue)
}

fun dynamoDbKeyOf(partitionValue: Any, sortValue: Any? = null): Key = dynamoDbKey {
    when (partitionValue) {
        is Number    -> partitionValue(partitionValue)
        is ByteArray -> partitionValue(partitionValue.toSdkBytes())
        else         -> partitionValue(partitionValue.toString())
    }
    sortValue?.let {
        when (sortValue) {
            is Number    -> sortValue(sortValue)
            is ByteArray -> sortValue(sortValue.toSdkBytes())
            else         -> sortValue(sortValue.toString())
        }
    }
}
