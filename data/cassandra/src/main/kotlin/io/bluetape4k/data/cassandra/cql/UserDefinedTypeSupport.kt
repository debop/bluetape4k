package io.bluetape4k.data.cassandra.cql

import com.datastax.oss.driver.api.core.CqlIdentifier
import com.datastax.oss.driver.api.core.type.UserDefinedType
import com.datastax.oss.driver.internal.core.type.UserDefinedTypeBuilder

inline fun userDefinedType(
    keyspaceId: CqlIdentifier,
    typeId: CqlIdentifier,
    initializer: UserDefinedTypeBuilder.() -> Unit,
): UserDefinedType {
    return UserDefinedTypeBuilder(keyspaceId, typeId).apply(initializer).build()
}

inline fun userDefinedType(
    keyspaceName: String,
    typeName: String,
    initializer: UserDefinedTypeBuilder.() -> Unit,
): UserDefinedType {
    return UserDefinedTypeBuilder(keyspaceName, typeName).apply(initializer).build()
}
