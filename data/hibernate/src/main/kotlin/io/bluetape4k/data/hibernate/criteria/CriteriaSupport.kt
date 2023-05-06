package io.bluetape4k.data.hibernate.criteria

import javax.persistence.criteria.AbstractQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

inline fun <reified T: Any> AbstractQuery<T>.from(): Root<T> = this.from(T::class.java)
fun <T, V> Root<T>.attribute(attribute: KProperty1<T, V>): Path<T> = this.get(attribute.name)


fun <T: Any> CriteriaBuilder.createQuery(clazz: KClass<T>): CriteriaQuery<T> = createQuery(clazz.java)
inline fun <reified T: Any> CriteriaBuilder.createQueryAs(): CriteriaQuery<T> = createQuery(T::class.java)

fun CriteriaBuilder.eq(x: Expression<*>, y: Any?): Predicate = this.equal(x, y)
fun CriteriaBuilder.eq(x: Expression<*>, y: Expression<*>): Predicate = this.equal(x, y)

fun CriteriaBuilder.ne(x: Expression<*>, y: Any?): Predicate = this.notEqual(x, y)
fun CriteriaBuilder.ne(x: Expression<*>, y: Expression<*>): Predicate = this.notEqual(x, y)

fun <T> CriteriaBuilder.inValues(expr: Expression<out T>): CriteriaBuilder.In<T> = this.`in`(expr)
