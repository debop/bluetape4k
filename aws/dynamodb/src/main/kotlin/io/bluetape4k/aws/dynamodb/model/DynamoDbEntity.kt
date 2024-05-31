package io.bluetape4k.aws.dynamodb.model

import io.bluetape4k.aws.dynamodb.model.DynamoDbEntity.Companion.ENTITY_ID_DELIMITER
import io.bluetape4k.aws.dynamodb.model.DynamoDbEntity.Companion.ENTITY_NAME_DELIMITER
import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.idgenerators.snowflake.GlobalSnowflake
import io.bluetape4k.idgenerators.uuid.TimebasedUuidGenerator
import io.bluetape4k.logging.KLogging
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.io.Serializable

interface DynamoDbEntity: Serializable {

    companion object: KLogging() {
        const val ENTITY_ID_DELIMITER = "#"
        const val ENTITY_NAME_DELIMITER = ":"

        val uuidGenerator = TimebasedUuidGenerator()
        val snowflake = GlobalSnowflake()
    }

    @get:DynamoDbPartitionKey
    val partitionKey: String

    @get:DynamoDbSortKey
    val sortKey: String

    val key: Key

    fun getUniqueLong(): Long = snowflake.nextId()

    fun getUniqueUuidString(): String = uuidGenerator.nextBase62String()
}

abstract class AbstractDynamoDbEntity: AbstractValueObject(), DynamoDbEntity {

    override val key: Key by lazy {
        Key.builder()
            .partitionValue(partitionKey)
            .sortValue(sortKey)
            .build()
    }

    override fun equalProperties(other: Any): Boolean {
        return other is DynamoDbEntity &&
                partitionKey == other.partitionKey &&
                sortKey == other.sortKey
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("partitionKey", partitionKey)
            .add("sortKey", sortKey)
    }
}

inline fun <reified T: DynamoDbEntity> T.makeKey(
    partitionKey: Any? = null,
    sortKey: Any? = null,
): String = buildString {
    append(T::class.simpleName)

    partitionKey
        ?.takeIf { it.toString().isNotBlank() }
        ?.let {
            append(ENTITY_NAME_DELIMITER)
            append(it)
        }
    sortKey
        ?.takeIf { it.toString().isNotBlank() }
        ?.let {
            append(ENTITY_ID_DELIMITER)
            append(it)
        }
}
