package io.bluetape4k.spring.util

import org.springframework.util.NumberUtils
import java.text.NumberFormat

inline fun <reified T: Number> String.parseNumber(): T =
    NumberUtils.parseNumber(this, T::class.java)

inline fun <reified T: Number> String.parseNumber(numberFormat: NumberFormat): T =
    NumberUtils.parseNumber(this, T::class.java, numberFormat)

fun <T: Number> String.parseNumber(targetClass: Class<T>): T =
    NumberUtils.parseNumber(this, targetClass)

fun <T: Number> String.parseNumber(targetClass: Class<T>, numberFormat: NumberFormat): T =
    NumberUtils.parseNumber(this, targetClass, numberFormat)
