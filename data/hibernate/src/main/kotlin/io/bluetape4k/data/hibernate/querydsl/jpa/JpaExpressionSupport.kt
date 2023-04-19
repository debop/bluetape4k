package io.bluetape4k.data.hibernate.querydsl.jpa

import com.querydsl.core.types.CollectionExpression
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.dsl.BeanPath
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.jpa.JPAExpressions

inline fun <reified U: BeanPath<out T>, T: Any> BeanPath<out T>.treat(): U {
    return JPAExpressions.treat(this, U::class.java)
}

fun <T: Comparable<T>> CollectionExpression<*, T>.avg(): ComparableExpression<T> {
    return JPAExpressions.avg(this)
}

fun <T: Comparable<T>> CollectionExpression<*, T>.max(): ComparableExpression<T> {
    return JPAExpressions.max(this)
}

fun <T: Comparable<T>> CollectionExpression<*, T>.min(): ComparableExpression<T> {
    return JPAExpressions.min(this)
}

fun EntityPath<*>.type(): StringExpression {
    return JPAExpressions.type(this)
}
