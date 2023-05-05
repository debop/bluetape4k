package io.bluetape4k.aws.dynamodb.model

import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput

inline fun ProvisionedThroughput(
    initializer: ProvisionedThroughput.Builder.() -> Unit,
): ProvisionedThroughput {
    return ProvisionedThroughput.builder().apply(initializer).build()
}

fun provisionedThroughputOf(
    readCapacityUnits: Long? = null,
    writeCapacityUnits: Long? = null,
): ProvisionedThroughput = ProvisionedThroughput {
    readCapacityUnits(readCapacityUnits)
    writeCapacityUnits(writeCapacityUnits)
}
