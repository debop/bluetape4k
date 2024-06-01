package io.bluetape4k.cassandra.cql

import com.datastax.oss.driver.api.core.type.DataType
import com.datastax.oss.driver.api.core.type.DataTypes
import com.datastax.oss.driver.api.core.type.ListType
import com.datastax.oss.driver.api.core.type.MapType
import com.datastax.oss.driver.api.core.type.SetType
import com.datastax.oss.driver.api.core.type.UserDefinedType

val DataType.isCollectionType: Boolean
    get() = this is ListType || this is SetType || this is MapType

val DataType.isNonFrozenUdt: Boolean
    get() = this is UserDefinedType && !this.isFrozen


fun DataType.potentiallyFreeze(): DataType {
    when (this) {
        is ListType -> {
            if (elementType.isCollectionType || elementType.isNonFrozenUdt) {
                return DataTypes.listOf(elementType.potentiallyFreeze(), this.isFrozen)
            }
        }

        is SetType  -> {
            if (elementType.isCollectionType || elementType.isNonFrozenUdt) {
                return DataTypes.setOf(elementType.potentiallyFreeze(), isFrozen)
            }
        }

        is MapType  -> {
            if (keyType.isCollectionType || valueType.isCollectionType || keyType.isNonFrozenUdt || valueType.isNonFrozenUdt) {
                return DataTypes.mapOf(keyType.potentiallyFreeze(), valueType.potentiallyFreeze(), isFrozen)
            }
        }
    }

    if (isNonFrozenUdt) {
        return (this as UserDefinedType).copy(true)
    }
    return this
}
