package io.bluetape4k.cassandra

import com.datastax.oss.driver.api.core.CqlIdentifier

fun String.toCqlIdentifier(): CqlIdentifier = CqlIdentifier.fromInternal(this)

fun CqlIdentifier.prettyCql(): String = asCql(true)
