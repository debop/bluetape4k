package io.bluetape4k.aws.dynamodb.model

import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedLocalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.Projection

inline fun enhancedGlobalSecondaryIndex(
    initializer: EnhancedGlobalSecondaryIndex.Builder.() -> Unit,
): EnhancedGlobalSecondaryIndex {
    return EnhancedGlobalSecondaryIndex.builder().apply(initializer).build()
}

fun enhancedGlobalSecondaryIndexOf(
    indexName: String,
    projection: Projection,
): EnhancedGlobalSecondaryIndex = enhancedGlobalSecondaryIndex {
    indexName(indexName)
    projection(projection)
}

inline fun enhancedLocalSecondaryIndex(
    initializer: EnhancedLocalSecondaryIndex.Builder.() -> Unit,
): EnhancedLocalSecondaryIndex {
    return EnhancedLocalSecondaryIndex.builder().apply(initializer).build()
}

fun enhancedLocalSecondaryIndexOf(
    indexName: String,
    projection: Projection,
): EnhancedLocalSecondaryIndex = enhancedLocalSecondaryIndex {
    indexName(indexName)
    projection(projection)
}
