package io.bluetape4k.data.hibernate.querydsl.core

import com.querydsl.core.types.ArrayConstructorExpression
import com.querydsl.core.types.ConstructorExpression
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Path
import com.querydsl.core.types.Projections
import com.querydsl.core.types.QBean
import com.querydsl.core.types.QList
import com.querydsl.core.types.QMap
import com.querydsl.core.types.QTuple
import kotlin.reflect.KClass

inline fun <reified T: Any> arrayProjectionOf(
    type: Class<Array<T>>,
    vararg exprs: Expression<T>,
): ArrayConstructorExpression<T> =
    Projections.array(type, *exprs)

inline fun <reified T: Any> beanProjectionOf(vararg exprs: Expression<*>): QBean<T> =
    Projections.bean(T::class.java, *exprs)

inline fun <reified T: Any> beanProjectionOf(bindings: Map<String, Expression<*>>): QBean<T> =
    Projections.bean(T::class.java, bindings)

fun <T: Any> Path<T>.beanProjectionOf(vararg exprs: Expression<*>): QBean<T> =
    Projections.bean(this, *exprs)

fun <T: Any> Path<T>.beanProjectionOf(bindings: Map<String, Expression<*>>): QBean<T> =
    Projections.bean(this, bindings)


inline fun <reified T: Any> constructorProjectionOf(vararg exprs: Expression<*>): ConstructorExpression<T> =
    Projections.constructor(T::class.java, *exprs)

inline fun <reified T: Any> constructorProjectionOf(
    paramTypes: Array<KClass<*>>,
    vararg exprs: Expression<*>,
): ConstructorExpression<T> =
    Projections.constructor(T::class.java, paramTypes.map { it.java }.toTypedArray(), *exprs)

inline fun <reified T: Any> constructorProjectionOf(
    paramTypes: Array<KClass<*>>,
    exprs: List<Expression<*>>,
): ConstructorExpression<T> =
    Projections.constructor(T::class.java, paramTypes.map { it.java }.toTypedArray(), exprs)


inline fun <reified T: Any> fieldProjectionOf(vararg exprs: Expression<*>): QBean<T> =
    Projections.fields(T::class.java, *exprs)

inline fun <reified T: Any> fieldProjectionOf(bindings: Map<String, Expression<*>>): QBean<T> =
    Projections.fields(T::class.java, bindings)

fun <T: Any> Path<T>.fieldProjectionOf(vararg exprs: Expression<*>): QBean<T> =
    Projections.fields(this, *exprs)

fun <T: Any> Path<T>.fieldProjectionOf(bindings: Map<String, Expression<*>>): QBean<T> =
    Projections.fields(this, bindings)

fun projectionListOf(vararg exprs: Expression<*>): QList = Projections.list(*exprs)
fun projectionListOf(exprs: List<Expression<*>>): QList = Projections.list(exprs)

fun projectionMapOf(vararg exprs: Expression<*>): QMap = Projections.map(*exprs)

fun projectionTupleOf(vararg exprs: Expression<*>): QTuple = Projections.tuple(*exprs)
fun projectionTupleOf(exprs: List<Expression<*>>): QTuple = Projections.tuple(exprs)
