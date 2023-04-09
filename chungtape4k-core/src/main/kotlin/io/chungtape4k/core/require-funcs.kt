package io.chungtape4k.core

import kotlin.contracts.contract

fun <T : Any> T?.requireNotNull(parameterName: String): T {
    contract {
        returns() implies (this@requireNotNull != null)
    }
    require(this != null) { "$parameterName[$this] must not be null." }
    return this
}

fun <T : Any> T?.requireNull(parameterName: String): T? {
    contract {
        returns() implies (this@requireNull == null)
    }
    require(this == null) { "$parameterName[$this] must be null." }
    return this
}

fun <T : CharSequence> T?.requireNotEmpty(parameterName: String): T {
    contract {
        returnsNotNull() implies (this@requireNotEmpty != null)
    }
    val self = this.requireNotNull(parameterName)
    require(self.isNotEmpty()) { "$parameterName[$self] must not be empty." }
    return self
}

fun <T : CharSequence> T?.requireNullOrEmpty(parameterName: String): T? {
    contract {
        returnsNotNull() implies (this@requireNullOrEmpty == null)
    }
    require(this.isNullOrEmpty()) { "$parameterName[$this] must be null or empty." }
    return this
}

fun <T : CharSequence> T?.requireNotBlank(parameterName: String): T {
    contract {
        returnsNotNull() implies (this@requireNotBlank != null)
    }
    val self = this.requireNotNull(parameterName)
    require(self.isNotBlank()) { "$parameterName[$self] must not be blank." }
    return self
}

fun <T : CharSequence> T?.requireNullOrBlank(parameterName: String): T? {
    contract {
        returnsNotNull() implies (this@requireNullOrBlank == null)
    }
    require(this.isNullOrEmpty()) { "$parameterName[$this] must be null or blank." }
    return this
}


fun <T : CharSequence> T?.requireContains(other: CharSequence, name: String): T {
    this.requireNotNull(name)
    require(this.contains(other)) { "$name[$this] must contain $other" }
    return this
}

fun <T : CharSequence> T?.requireStartsWith(prefix: CharSequence, name: String, ignoreCase: Boolean = false): T {
    this.requireNotNull(name)
    require(this.startsWith(prefix, ignoreCase)) { "$name[$this] must be starts with $prefix" }
    return this
}

fun <T : CharSequence> T?.requireEndsWith(prefix: CharSequence, name: String, ignoreCase: Boolean = false): T {
    this.requireNotNull(name)
    require(this.endsWith(prefix, ignoreCase)) { "$name[$this] must be ends with $prefix" }
    return this
}

fun <T> T.requireEquals(expected: T, name: String): T = apply {
    require(this == expected) { "$name[$this] must be equal to $expected" }
}

@Deprecated("use requireGt", replaceWith = ReplaceWith("requireGt(expected, name)"))
fun <T : Comparable<T>> T.requireGreaterThan(expected: T, name: String): T = apply {
    require(this > expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.requireGt(expected: T, name: String): T = apply {
    require(this > expected) { "$name[$this] must be greater than $expected." }
}

@Deprecated("use requireGe", replaceWith = ReplaceWith("requireGe(expected, name)"))
fun <T : Comparable<T>> T.requireGreaterThanOrEqualTo(expected: T, name: String): T = apply {
    require(this >= expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.requireGe(expected: T, name: String): T = apply {
    require(this >= expected) { "$name[$this] must be greater than $expected." }
}

@Deprecated("use requireLt", replaceWith = ReplaceWith("requireLt(expected, name)"))
fun <T : Comparable<T>> T.requireLessThan(expected: T, name: String): T = apply {
    require(this < expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.requireLt(expected: T, name: String): T = apply {
    require(this < expected) { "$name[$this] must be greater than $expected." }
}

@Deprecated("use requireLe", replaceWith = ReplaceWith("requireLe(expected, name)"))
fun <T : Comparable<T>> T.requireLessThanOrEqualTo(expected: T, name: String): T = apply {
    require(this <= expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.requireLe(expected: T, name: String): T = apply {
    require(this <= expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.requireInRange(start: T, endInclusive: T, name: String) = apply {
    require(this in start..endInclusive) { "$name[$this] must be in range ($start .. $endInclusive)" }
}

fun <T : Comparable<T>> T.requireInOpenRange(start: T, endExclusive: T, name: String): T = apply {
    require(this >= start && this < endExclusive) { "$start <= $name[$this] < $endExclusive" }
}

fun <T> T.requireZeroOrPositiveNumber(name: String): T where T : Number, T : Comparable<T> = apply {
    toDouble().requireGe(0.0, name)
}

fun <T> T.requirePositiveNumber(name: String): T where T : Number, T : Comparable<T> = apply {
    toDouble().requireGt(0.0, name)
}

fun <T> T.requireZeroOrNegativeNumber(name: String): T where T : Number, T : Comparable<T> = apply {
    toDouble().requireLe(0.0, name)
}

fun <T> T.requireNegativeNumber(name: String): T where T : Number, T : Comparable<T> = apply {
    toDouble().requireLt(0.0, name)
}

fun <T> Collection<T>?.requireNotEmpty(name: String) = apply {
    require(!this.isNullOrEmpty()) { "$name[$this] must not be null or empty." }
}


fun <K, V> Map<K, V>?.requireNotEmpty(name: String) = apply {
    require(!this.isNullOrEmpty()) { "$name must not be null or empty." }
}

fun <K, V> Map<K, V>?.requireHasKey(key: K, name: String) = apply {
    requireNotEmpty(name)
    require(this!!.containsKey(key)) { "$name require contains key $key" }
}

fun <K, V> Map<K, V>?.requireHasValue(value: V, name: String) = apply {
    requireNotEmpty(name)
    require(this!!.containsValue(value)) { "$name require contains value $value" }
}

fun <K, V> Map<K, V>?.requireContains(key: K, value: V, name: String) = apply {
    requireNotEmpty(name)
    require(this!![key] == value) { "$name require contains ($key, $value)" }
}
