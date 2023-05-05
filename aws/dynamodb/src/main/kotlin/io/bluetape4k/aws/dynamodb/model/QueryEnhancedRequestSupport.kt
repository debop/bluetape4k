package io.bluetape4k.aws.dynamodb.model

import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

inline fun QueryEhancedRequest(
    initializer: QueryEnhancedRequest.Builder.() -> Unit,
): QueryEnhancedRequest {
    return QueryEnhancedRequest.builder().apply(initializer).build()
}

fun queryEhnahcedRequestOf(
    queryConditional: QueryConditional? = null,
    exclusiveStartKey: Map<String, AttributeValue>? = null,
    scanIndexForward: Boolean? = null,
    limit: Int? = null,
    consistentRead: Boolean? = null,
    filterExpression: Expression? = null,
    attributesToProject: Collection<String>? = null,
): QueryEnhancedRequest = QueryEhancedRequest {
    queryConditional(queryConditional)
    exclusiveStartKey(exclusiveStartKey)
    scanIndexForward(scanIndexForward)
    limit(limit)
    consistentRead(consistentRead)
    filterExpression(filterExpression)
    attributesToProject(attributesToProject)
}
