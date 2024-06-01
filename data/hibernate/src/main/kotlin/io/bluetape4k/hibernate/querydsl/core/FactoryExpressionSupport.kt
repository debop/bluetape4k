package io.bluetape4k.hibernate.querydsl.core

import com.querydsl.core.types.Expression
import com.querydsl.core.types.FactoryExpression
import com.querydsl.core.types.FactoryExpressionUtils

fun List<Expression<*>>.wrap(): FactoryExpression<*> =
    FactoryExpressionUtils.wrap(this)

fun <T> FactoryExpression<T>.wrap(conversions: List<Expression<*>>): FactoryExpression<T> =
    FactoryExpressionUtils.wrap(this, conversions)

fun <T> FactoryExpression<T>.wrap(): FactoryExpression<T> =
    FactoryExpressionUtils.wrap(this)
