@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package io.bluetape4k.hibernate.querydsl.core

import com.querydsl.core.types.Expression
import com.querydsl.core.types.Ops
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.core.types.dsl.SimpleExpression
import com.querydsl.core.types.dsl.StringExpression


/**
 * Get a negation of this boolean expression
 *
 * @return !this
 */
operator fun Expression<Boolean>.not(): BooleanExpression {
    return Expressions.booleanOperation(Ops.NOT, this)
}

/**
 * Get an intersection of this and the given expression
 *
 * @param predicate right hand side of the union
 * @return this and right
 */
infix fun Expression<Boolean>.and(predicate: Expression<Boolean>): BooleanExpression {
    return Expressions.booleanOperation(Ops.AND, this, predicate)
}

/**
 * Get a union of this and the given expression
 *
 * @param predicate right hand side of the union
 * @return this || right
 */
infix fun Expression<Boolean>.or(predicate: Expression<Boolean>): BooleanExpression {
    return Expressions.booleanOperation(Ops.OR, this, predicate)
}

/**
 * Get a union of this and the given expression
 *
 * @param predicate right hand side of the union
 * @return this || right
 */
infix fun Expression<Boolean>.xor(predicate: Expression<Boolean>): BooleanExpression {
    return Expressions.booleanOperation(Ops.XOR, this, predicate)
}

/**
 * Get a union of this and the given expression
 *
 * @param predicate right hand side of the union
 * @return this || right
 */
infix fun Expression<Boolean>.xnor(predicate: Expression<Boolean>): BooleanExpression {
    return Expressions.booleanOperation(Ops.XNOR, this, predicate)
}

/**
 * Get the negation of this expression
 *
 * @return this * -1
 */
operator fun <T> Expression<T>.unaryMinus(): NumberExpression<T> where T: Comparable<*>, T: Number {
    return Expressions.numberOperation(type, Ops.NEGATE, this)
}

/**
 * Get the sum of this and right
 *
 * @return this + right
 */
operator fun <T, V> Expression<T>.plus(other: Expression<V>): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.ADD, this, other)
}

/**
 * Get the difference of this and right
 *
 * @return this - right
 */
operator fun <T, V> Expression<T>.minus(other: Expression<V>): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.SUB, this, other)
}

/**
 * Get the result of the operation this * right
 *
 * @return this * right
 */
operator fun <T, V> Expression<T>.times(other: Expression<V>): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.MULT, this, other)
}

/**
 * Get the result of the operation this / right
 *
 * @return this / right
 */
operator fun <T, V> Expression<T>.div(other: Expression<V>): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.DIV, this, other)
}


/**
 * Get the result of the operation this / right
 *
 * @return this / right
 */
operator fun <T, V> NumberExpression<T>.div(other: Expression<V>): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number, V: Comparable<*> {
    return this.divide(other)
}

/**
 * Get the result of the operation this % right
 *
 * @return this % right
 */
operator fun <T, V> Expression<T>.rem(other: Expression<V>): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.MOD, this, other)
}

/**
 * Get the sum of this and right
 *
 * @return this + right
 */
operator fun <T, V> Expression<T>.plus(other: V): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.ADD, this, Expressions.constant(other))
}

/**
 * Get the difference of this and right
 *
 * @return this - right
 */
operator fun <T, V> Expression<T>.minus(other: V): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.SUB, this, Expressions.constant(other))
}

/**
 * Get the result of the operation this * right
 *
 * @return this * right
 */
operator fun <T, V> Expression<T>.times(other: V): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.MULT, this, Expressions.constant(other))
}

/**
 * Get the result of the operation this / right
 *
 * @return this / right
 */
operator fun <T, V> Expression<T>.div(other: V): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.DIV, this, Expressions.constant(other))
}

/**
 * Get the result of the operation this / right
 *
 * @return this / right
 */
operator fun <T, V> NumberExpression<T>.div(other: V): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number, V: Comparable<*> {
    return this.divide(other)
}

/**
 * Get the result of the operation this % right
 *
 * @return this % right
 */
operator fun <T, V> Expression<T>.rem(other: V): NumberExpression<T> where T: Comparable<*>, T: Number, V: Number {
    return Expressions.numberOperation(type, Ops.MOD, this, Expressions.constant(other))
}

/**
 * Get the concatenation of this and str
 *
 * @return this + str
 */
operator fun Expression<String>.plus(x: Expression<String>): StringExpression {
    return Expressions.stringOperation(Ops.CONCAT, this, x)
}

/**
 * Get the concatenation of this and str
 *
 * @return this + str
 */
operator fun Expression<String>.plus(x: String): StringExpression {
    return Expressions.stringOperation(Ops.CONCAT, this, Expressions.constant(x))
}

/**
 * Get the character at the given index
 *
 * @param x
 * @return this.charAt(x)
 * @see java.lang.String#charAt(int)
 */
operator fun Expression<String>.get(x: Expression<Int>): SimpleExpression<Character> {
    return Expressions.simpleOperation(Character::class.java, Ops.CHAR_AT, this, x)
}

/**
 * Get the character at the given index
 *
 * @param x
 * @return this.charAt(x)
 * @see java.lang.String#charAt(int)
 */
operator fun Expression<String>.get(x: Int): SimpleExpression<Character> {
    return Expressions.simpleOperation(Character::class.java, Ops.CHAR_AT, this, Expressions.constant(x))
}
