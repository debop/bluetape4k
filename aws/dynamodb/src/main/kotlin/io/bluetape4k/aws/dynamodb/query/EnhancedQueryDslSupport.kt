package io.bluetape4k.aws.dynamodb.query

import io.bluetape4k.aws.dynamodb.model.DynamoDbEntity
import io.bluetape4k.aws.dynamodb.model.dynamoDbKeyOf
import io.bluetape4k.aws.dynamodb.model.queryEnhancedRequest
import io.bluetape4k.core.requireNotNull
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue


// @DynamoDslMarker
fun <T: DynamoDbEntity> queryEnhancedRequest(
    initializer: EnhancedQueryBuilderKt<T>.() -> Unit,
): QueryEnhancedRequest {
    return EnhancedQueryBuilderKt<T>().apply(initializer).build()
}

@DynamoDslMarker
class EnhancedQueryBuilderKt<T: Any> {

    companion object: KLogging()

    var primaryKey: PrimaryKey? = null
    var sortKey: SortKey? = null
    var filtering: RootFilter? = null
    var scanIndexForward: Boolean = true
    var lastEvaluatedKey: Map<String, AttributeValue>? = null

    fun build(): QueryEnhancedRequest {
        log.debug { "Start query ...  primaryKey=$primaryKey, sortKey=$sortKey" }
        primaryKey.requireNotNull("primaryKey")

        return queryEnhancedRequest {

            val conditional = sortKey?.let { sk: SortKey ->

                when (sk.comparisonOperator) {
                    is BeginsWith          -> QueryConditional.sortBeginsWith(
                        dynamoDbKeyOf(primaryKey!!.equals.right, sk.comparisonOperator.right)
                    )
                    is GreaterThan         -> QueryConditional.sortGreaterThan(
                        dynamoDbKeyOf(primaryKey!!.equals.right, sk.comparisonOperator.right)
                    )
                    is GreaterThanOrEquals -> QueryConditional.sortGreaterThanOrEqualTo(
                        dynamoDbKeyOf(primaryKey!!.equals.right, sk.comparisonOperator.right)
                    )
                    is LessThan            -> QueryConditional.sortLessThan(
                        dynamoDbKeyOf(primaryKey!!.equals.right, sk.comparisonOperator.right)
                    )
                    is LessThanOrEquals    -> QueryConditional.sortLessThanOrEqualTo(
                        dynamoDbKeyOf(primaryKey!!.equals.right, sk.comparisonOperator.right)
                    )
                    is Between             -> QueryConditional.sortBetween(
                        dynamoDbKeyOf(sk.sortKeyName, sk.comparisonOperator.left.toString()),
                        dynamoDbKeyOf(sk.sortKeyName, sk.comparisonOperator.right.toString())
                    )
                    else                   ->
                        throw UnsupportedOperationException("Unknown comparison operator: ${sk.comparisonOperator}")
                }
            } ?: QueryConditional.keyEqualTo(dynamoDbKeyOf(primaryKey!!.equals.right))

            queryConditional(conditional)

            filtering?.let { filter ->
                val props = filter.getFilterRequestProperties()
                filterExpression(props.toExpression())
            }

            scanIndexForward(scanIndexForward)
            lastEvaluatedKey?.let { exclusiveStartKey(it) }
        }
    }
}

// @DynamoDslMarker
fun <T: DynamoDbEntity> EnhancedQueryBuilderKt<T>.primaryKey(
    keyName: String = "primaryKey",
    @BuilderInference initializer: PrimaryKeyBuilder.() -> Unit,
) {
    primaryKey = PrimaryKeyBuilder(keyName).apply(initializer).build()
}

// @DynamoDslMarker
fun <T: DynamoDbEntity> EnhancedQueryBuilderKt<T>.sortKey(
    keyName: String = "sortKey",
    @BuilderInference initializer: SortKeyBuilder.() -> Unit,
) {
    sortKey = SortKeyBuilder(keyName).apply(initializer).build()
}

// @DynamoDslMarker
fun <T: DynamoDbEntity> EnhancedQueryBuilderKt<T>.filtering(
    @BuilderInference initializer: RootFilterBuilder.() -> Unit,
) {
    filtering = RootFilterBuilder().apply(initializer).build()
}
