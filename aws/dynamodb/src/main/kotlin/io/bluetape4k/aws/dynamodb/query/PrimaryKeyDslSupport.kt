package io.bluetape4k.aws.dynamodb.query

@DynamoDslMarker
data class PrimaryKey(val keyName: String = "primaryKey", val equals: Equals)

@DynamoDslMarker
class PrimaryKeyBuilder(val keyName: String = "primaryKey") {
    var comparator: Equals? = null
    fun build(): PrimaryKey = PrimaryKey(keyName, comparator!!)
}

fun PrimaryKeyBuilder.eq(value: Any) {
    comparator = Equals(value)
}
