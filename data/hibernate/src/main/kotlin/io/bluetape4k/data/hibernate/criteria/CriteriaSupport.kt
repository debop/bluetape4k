package io.bluetape4k.data.hibernate.criteria

import jakarta.persistence.criteria.AbstractQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
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
