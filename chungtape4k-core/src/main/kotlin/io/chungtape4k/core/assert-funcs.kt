package io.chungtape4k.core

import kotlin.contracts.contract

fun <T : Any> T?.assertNotNull(parameterName: String): T {
    contract {
        returns() implies (this@assertNotNull != null)
    }
    assert(this != null) { "$parameterName[$this] must not be null." }
    return this!!
}

fun <T : Any> T?.assertNull(parameterName: String): T? {
    contract {
        returns() implies (this@assertNull == null)
    }
    assert(this == null) { "$parameterName[$this] must be null." }
    return this
}

fun <T : CharSequence> T?.assertNotEmpty(parameterName: String): T {
    contract {
        returnsNotNull() implies (this@assertNotEmpty != null)
    }
    val self = this.assertNotNull(parameterName)
    assert(self.isNotEmpty()) { "$parameterName[$self] must not be empty." }
    return self
}

fun <T : CharSequence> T?.assertNullOrEmpty(parameterName: String): T? {
    contract {
        returnsNotNull() implies (this@assertNullOrEmpty == null)
    }
    assert(this.isNullOrEmpty()) { "$parameterName[$this] must be null or empty." }
    return this
}

fun <T : CharSequence> T?.assertNotBlank(parameterName: String): T {
    contract {
        returnsNotNull() implies (this@assertNotBlank != null)
    }
    val self = this.assertNotNull(parameterName)
    assert(self.isNotBlank()) { "$parameterName[$self] must not be blank." }
    return self
}

fun <T : CharSequence> T?.assertNullOrBlank(parameterName: String): T? {
    contract {
        returnsNotNull() implies (this@assertNullOrBlank == null)
    }
    assert(this.isNullOrEmpty()) { "$parameterName[$this] must be null or blank." }
    return this
}


fun <T : CharSequence> T?.assertContains(other: CharSequence, name: String): T {
    this.assertNotNull(name)
    assert(this.contains(other)) { "$name[$this] must contain $other" }
    return this
}

fun <T : CharSequence> T?.assertStartsWith(prefix: CharSequence, name: String, ignoreCase: Boolean = false): T {
    this.assertNotNull(name)
    assert(this.startsWith(prefix, ignoreCase)) { "$name[$this] must be starts with $prefix" }
    return this
}

fun <T : CharSequence> T?.assertEndsWith(prefix: CharSequence, name: String, ignoreCase: Boolean = false): T {
    this.assertNotNull(name)
    assert(this.endsWith(prefix, ignoreCase)) { "$name[$this] must be ends with $prefix" }
    return this
}

fun <T> T.assertEquals(expected: T, name: String): T = apply {
    assert(this == expected) { "$name[$this] must be equal to $expected" }
}

@Deprecated("use assertGt", replaceWith = ReplaceWith("assertGt(expected, name)"))
fun <T : Comparable<T>> T.assertGreaterThan(expected: T, name: String): T = apply {
    assert(this > expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.assertGt(expected: T, name: String): T = apply {
    assert(this > expected) { "$name[$this] must be greater than $expected." }
}

@Deprecated("use assertGe", replaceWith = ReplaceWith("assertGe(expected, name)"))
fun <T : Comparable<T>> T.assertGreaterThanOrEqualTo(expected: T, name: String): T = apply {
    assert(this >= expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.assertGe(expected: T, name: String): T = apply {
    assert(this >= expected) { "$name[$this] must be greater than $expected." }
}

@Deprecated("use assertLt", replaceWith = ReplaceWith("assertLt(expected, name)"))
fun <T : Comparable<T>> T.assertLessThan(expected: T, name: String): T = apply {
    assert(this < expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.assertLt(expected: T, name: String): T = apply {
    assert(this < expected) { "$name[$this] must be greater than $expected." }
}

@Deprecated("use assertLe", replaceWith = ReplaceWith("assertLe(expected, name)"))
fun <T : Comparable<T>> T.assertLessThanOrEqualTo(expected: T, name: String): T = apply {
    assert(this <= expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.assertLe(expected: T, name: String): T = apply {
    assert(this <= expected) { "$name[$this] must be greater than $expected." }
}

fun <T : Comparable<T>> T.assertInRange(start: T, endInclusive: T, name: String) = apply {
    assert(this in start..endInclusive) { "$name[$this] must be in range ($start .. $endInclusive)" }
}

fun <T : Comparable<T>> T.assertInOpenRange(start: T, endExclusive: T, name: String): T = apply {
    assert(this >= start && this < endExclusive) { "$start <= $name[$this] < $endExclusive" }
}

fun <T> T.assertZeroOrPositiveNumber(name: String): T where T : Number, T : Comparable<T> = apply {
    toDouble().assertGe(0.0, name)
}

fun <T> T.assertPositiveNumber(name: String): T where T : Number, T : Comparable<T> = apply {
    toDouble().assertGt(0.0, name)
}

fun <T> T.assertZeroOrNegativeNumber(name: String): T where T : Number, T : Comparable<T> = apply {
    toDouble().assertLe(0.0, name)
}

fun <T> T.assertNegativeNumber(name: String): T where T : Number, T : Comparable<T> = apply {
    toDouble().assertLt(0.0, name)
}

fun <T> Collection<T>?.assertNotEmpty(name: String) = apply {
    assert(!this.isNullOrEmpty()) { "$name[$this] must not be null or empty." }
}


fun <K, V> Map<K, V>?.assertNotEmpty(name: String) = apply {
    assert(!this.isNullOrEmpty()) { "$name must not be null or empty." }
}

fun <K, V> Map<K, V>?.assertHasKey(key: K, name: String) = apply {
    assertNotEmpty(name)
    assert(this!!.containsKey(key)) { "$name require contains key $key" }
}

fun <K, V> Map<K, V>?.assertHasValue(value: V, name: String) = apply {
    assertNotEmpty(name)
    assert(this!!.containsValue(value)) { "$name require contains value $value" }
}

fun <K, V> Map<K, V>?.assertContains(key: K, value: V, name: String) = apply {
    assertNotEmpty(name)
    assert(this!![key] == value) { "$name require contains ($key, $value)" }
}
