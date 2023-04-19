package io.bluetape4k.data.hibernate.querydsl.jpa

import com.querydsl.core.types.Expression
import com.querydsl.jpa.Conversions

fun <T> Expression<T>.convert(): Expression<T> =
    Conversions.convert(this)

fun <T> Expression<T>.convertForNativeQuery(): Expression<T> =
    Conversions.convertForNativeQuery(this)
