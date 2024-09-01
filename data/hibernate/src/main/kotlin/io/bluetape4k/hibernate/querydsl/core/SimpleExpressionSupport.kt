package io.bluetape4k.hibernate.querydsl.core

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.SimpleExpression

fun <T> SimpleExpression<T>.inValues(vararg rights: T): BooleanExpression = `in`(*rights)
