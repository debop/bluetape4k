package io.bluetape4k.spring.beans

import org.springframework.beans.PropertyAccessorUtils

/**
 * 지정한 property path의 실제 property name을 추출합니다.
 */
fun String.getPropertyName(): String =
    PropertyAccessorUtils.getPropertyName(this)

/**
 * 지정한 property path가  indexed 나 nested property 인지 나타냅니다.
 */
fun String.isNestedOrIndexedProperty(): Boolean =
    PropertyAccessorUtils.isNestedOrIndexedProperty(this)

/**
 * Determine the first nested property separator in the given property path, ignoring dots in keys (like `map["my.key"]`).
 */
fun String.getFirstNestedPropertySeparatorIndex(): Int =
    PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(this)

/**
 * Determine the last nested property separator in the given property path, ignoring dots in keys (like `map["my.key"]`).
 */
fun String.getLastNestedPropertySeparatorIndex(): Int =
    PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(this)

/**
 * Determine whether the given registered path matches the given property path,
 * either indicating the property itself or an indexed element of the property.
 *
 * @param propertyPath the property path (typically without index)
 */
fun String.matchesProperty(propertyPath: String): Boolean =
    PropertyAccessorUtils.matchesProperty(this, propertyPath)

/**
 * Determine the canonical name for the given property path.
 * Removes surrounding quotes from map keys:<br>
 * {@code map['key']} -> {@code map[`key`]}<br>
 * {@code map["key"]} -> {@code map[`key`]}
 */
fun String.canonicalPropertyName(): String =
    PropertyAccessorUtils.canonicalPropertyName(this)

/**
 * Determine the canonical names for the given property paths.
 *
 * @return the canonical representation of the property paths
 * (as array of the same size)
 */
fun Array<String>.canonicalPropertyNames(): Array<String>? =
    PropertyAccessorUtils.canonicalPropertyNames(this)
