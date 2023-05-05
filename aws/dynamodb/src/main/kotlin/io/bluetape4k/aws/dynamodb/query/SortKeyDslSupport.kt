package io.bluetape4k.aws.dynamodb.query

data class SortKey(
    val sortKeyName: String = "sortKey",
    val comparisonOperator: DynamoComparator,
): ComparableBuilder


class SortKeyBuilder(val keyName: String = "sortKey") {
    var comparator: DynamoComparator? = null
    fun build(): SortKey = SortKey(keyName, comparator!!)
}

fun SortKeyBuilder.between(values: Pair<Any, Any>) {
    comparator = Between(values.first, values.second)
}

fun SortKeyBuilder.eq(value: Any) {
    comparator = Equals(value)
}
