package io.bluetape4k.data.cassandra.querybuilder

import com.datastax.oss.driver.api.core.CqlIdentifier
import com.datastax.oss.driver.api.core.type.UserDefinedType
import com.datastax.oss.driver.api.querybuilder.BindMarker
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.Raw

fun String?.bindMarker(): BindMarker = QueryBuilder.bindMarker(this)
fun CqlIdentifier?.bindMarker(): BindMarker = QueryBuilder.bindMarker(this)

fun String.raw(): Raw = QueryBuilder.raw(this)

fun String.udt(): UserDefinedType = QueryBuilder.udt(this)
fun CqlIdentifier.udt(): UserDefinedType = QueryBuilder.udt(this)
