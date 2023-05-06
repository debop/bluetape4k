package io.bluetape4k.data.hibernate.querydsl.core

import com.querydsl.core.Tuple
import com.querydsl.core.types.CollectionExpression
import com.querydsl.core.types.Expression
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.NullExpression
import com.querydsl.core.types.Operator
import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.Template
import com.querydsl.core.types.dsl.ArrayPath
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.BooleanOperation
import com.querydsl.core.types.dsl.BooleanPath
import com.querydsl.core.types.dsl.BooleanTemplate
import com.querydsl.core.types.dsl.CollectionPath
import com.querydsl.core.types.dsl.ComparableEntityPath
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.core.types.dsl.ComparableOperation
import com.querydsl.core.types.dsl.ComparablePath
import com.querydsl.core.types.dsl.ComparableTemplate
import com.querydsl.core.types.dsl.DateExpression
import com.querydsl.core.types.dsl.DateOperation
import com.querydsl.core.types.dsl.DatePath
import com.querydsl.core.types.dsl.DateTemplate
import com.querydsl.core.types.dsl.DateTimeExpression
import com.querydsl.core.types.dsl.DateTimePath
import com.querydsl.core.types.dsl.DateTimeTemplate
import com.querydsl.core.types.dsl.DslOperation
import com.querydsl.core.types.dsl.DslPath
import com.querydsl.core.types.dsl.DslTemplate
import com.querydsl.core.types.dsl.EnumExpression
import com.querydsl.core.types.dsl.EnumOperation
import com.querydsl.core.types.dsl.EnumPath
import com.querydsl.core.types.dsl.EnumTemplate
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.ListPath
import com.querydsl.core.types.dsl.MapPath
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.core.types.dsl.NumberOperation
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.NumberTemplate
import com.querydsl.core.types.dsl.SetPath
import com.querydsl.core.types.dsl.SimpleExpression
import com.querydsl.core.types.dsl.SimpleOperation
import com.querydsl.core.types.dsl.SimplePath
import com.querydsl.core.types.dsl.SimpleTemplate
import com.querydsl.core.types.dsl.StringExpression
import com.querydsl.core.types.dsl.StringOperation
import com.querydsl.core.types.dsl.StringPath
import com.querydsl.core.types.dsl.StringTemplate
import com.querydsl.core.types.dsl.TimeExpression
import com.querydsl.core.types.dsl.TimeOperation
import com.querydsl.core.types.dsl.TimePath
import com.querydsl.core.types.dsl.TimeTemplate
import java.sql.Time
import java.util.*


fun currentDateExpr(): DateExpression<Date> = Expressions.currentDate()
fun currentTimeExpr(): TimeExpression<Time> = Expressions.currentTime()
fun currentTimestampExpr(): DateTimeExpression<Date> = Expressions.currentTimestamp()

fun <D> Expression<D>.alias(alias: Path<D>): SimpleExpression<D> =
    Expressions.`as`(this, alias)

fun Collection<BooleanExpression>.all(): BooleanExpression = Expressions.allOf(*this.toTypedArray())
fun Collection<BooleanExpression>.any(): BooleanExpression = Expressions.anyOf(*this.toTypedArray())

fun <T> T.toExpression(): Expression<T> = Expressions.constant(this)
fun <T> T.toExpression(alias: Path<T>): SimpleExpression<T> = Expressions.constantAs(this, alias)

// SimpleTemplate

inline fun <reified T: Any> simpleTemplateOf(template: String, vararg args: Any?): SimpleTemplate<T> =
    Expressions.template(T::class.java, template, *args)

inline fun <reified T: Any> simpleTemplateOf(template: String, args: List<*>): SimpleTemplate<T> =
    Expressions.template(T::class.java, template, args)

inline fun <reified T: Any> Template.simpleTemplate(vararg args: Any?): SimpleTemplate<T> =
    Expressions.template(T::class.java, this, *args)

inline fun <reified T: Any> Template.simpleTemplate(args: List<*>): SimpleTemplate<T> =
    Expressions.template(T::class.java, this, args)

// DslTemplate

inline fun <reified T: Any> dslTemplateOf(template: String, vararg args: Any?): DslTemplate<T> =
    Expressions.dslTemplate(T::class.java, template, *args)

inline fun <reified T: Any> dslTemplateOf(template: String, args: List<*>): DslTemplate<T> =
    Expressions.dslTemplate(T::class.java, template, args)

inline fun <reified T: Any> Template.dslTemplate(vararg args: Any?): DslTemplate<T> =
    Expressions.dslTemplate(T::class.java, this, *args)

inline fun <reified T: Any> Template.dslTemplate(args: List<*>): DslTemplate<T> =
    Expressions.dslTemplate(T::class.java, this, args)

// ComparableTemplate

inline fun <reified T: Comparable<*>> comparableTemplateOf(template: String, vararg args: Any?): ComparableTemplate<T> =
    Expressions.comparableTemplate(T::class.java, template, *args)

inline fun <reified T: Comparable<*>> comparableTemplateOf(template: String, args: List<*>): ComparableTemplate<T> =
    Expressions.comparableTemplate(T::class.java, template, args)

inline fun <reified T: Comparable<*>> Template.comparableTemplate(vararg args: Any?): ComparableTemplate<T> =
    Expressions.comparableTemplate(T::class.java, this, *args)

inline fun <reified T: Comparable<*>> Template.comparableTemplate(args: List<*>): ComparableTemplate<T> =
    Expressions.comparableTemplate(T::class.java, this, args)

// DateTemplate

inline fun <reified T: Comparable<*>> dateTemplateOf(template: String, vararg args: Any?): DateTemplate<T> =
    Expressions.dateTemplate(T::class.java, template, *args)

inline fun <reified T: Comparable<*>> dateTemplateOf(template: String, args: List<*>): DateTemplate<T> =
    Expressions.dateTemplate(T::class.java, template, args)

inline fun <reified T: Comparable<*>> Template.dateTemplate(vararg args: Any?): DateTemplate<T> =
    Expressions.dateTemplate(T::class.java, this, *args)

inline fun <reified T: Comparable<*>> Template.dateTemplate(args: List<*>): DateTemplate<T> =
    Expressions.dateTemplate(T::class.java, this, args)

// DateTimeTemplate

inline fun <reified T: Comparable<*>> dateTimeTemplateOf(template: String, vararg args: Any?): DateTimeTemplate<T> =
    Expressions.dateTimeTemplate(T::class.java, template, *args)

inline fun <reified T: Comparable<*>> dateTimeTemplateOf(template: String, args: List<*>): DateTimeTemplate<T> =
    Expressions.dateTimeTemplate(T::class.java, template, args)

inline fun <reified T: Comparable<*>> Template.dateTimeTemplate(vararg args: Any?): DateTimeTemplate<T> =
    Expressions.dateTimeTemplate(T::class.java, this, *args)

inline fun <reified T: Comparable<*>> Template.dateTimeTemplate(args: List<*>): DateTimeTemplate<T> =
    Expressions.dateTimeTemplate(T::class.java, this, args)

// TimeTemplate

inline fun <reified T: Comparable<*>> timeTemplateOf(template: String, vararg args: Any?): TimeTemplate<T> =
    Expressions.timeTemplate(T::class.java, template, *args)

inline fun <reified T: Comparable<*>> timeTemplateOf(template: String, args: List<*>): TimeTemplate<T> =
    Expressions.timeTemplate(T::class.java, template, args)

inline fun <reified T: Comparable<*>> Template.timeTemplate(vararg args: Any?): TimeTemplate<T> =
    Expressions.timeTemplate(T::class.java, this, *args)

inline fun <reified T: Comparable<*>> Template.timeTemplate(args: List<*>): TimeTemplate<T> =
    Expressions.timeTemplate(T::class.java, this, args)

// EnumTemplate

inline fun <reified T: Enum<T>> enumTemplateOf(template: String, vararg args: Any?): EnumTemplate<T> =
    Expressions.enumTemplate(T::class.java, template, *args)

inline fun <reified T: Enum<T>> enumTemplateOf(template: String, args: List<*>): EnumTemplate<T> =
    Expressions.enumTemplate(T::class.java, template, args)

inline fun <reified T: Enum<T>> Template.enumTemplate(vararg args: Any?): EnumTemplate<T> =
    Expressions.enumTemplate(T::class.java, this, *args)

inline fun <reified T: Enum<T>> Template.enumTemplate(args: List<*>): EnumTemplate<T> =
    Expressions.enumTemplate(T::class.java, this, args)

// NumberTemplate

inline fun <reified T> numberTemplateOf(
    template: String,
    vararg args: Any?,
): NumberTemplate<T> where T: Number, T: Comparable<*> =
    Expressions.numberTemplate(T::class.java, template, *args)

inline fun <reified T> numberTemplateOf(
    template: String,
    args: List<*>,
): NumberTemplate<T> where T: Number, T: Comparable<*> =
    Expressions.numberTemplate(T::class.java, template, args)

inline fun <reified T> Template.numberTemplate(vararg args: Any?): NumberTemplate<T> where T: Number, T: Comparable<*> =
    Expressions.numberTemplate(T::class.java, this, *args)

inline fun <reified T> Template.numberTemplate(args: List<*>): NumberTemplate<T> where T: Number, T: Comparable<*> =
    Expressions.numberTemplate(T::class.java, this, args)

// StringTemplate

fun stringTemplateOf(template: String, vararg args: Any?): StringTemplate =
    Expressions.stringTemplate(template, *args)

fun simpleTemplateOf(template: String, args: List<*>): StringExpression =
    Expressions.stringTemplate(template, args)

fun Template.stringTemplate(vararg args: Any?): StringExpression =
    Expressions.stringTemplate(this, *args)

fun Template.stringTemplate(args: List<*>): StringExpression =
    Expressions.stringTemplate(this, args)

// BooleanTemplate

fun booleanTemplateOf(template: String, vararg args: Any?): BooleanTemplate =
    Expressions.booleanTemplate(template, *args)

fun booleanTemplateOf(template: String, args: List<*>): BooleanTemplate =
    Expressions.booleanTemplate(template, args)

fun Template.booleanTemplate(vararg args: Any?): BooleanTemplate =
    Expressions.booleanTemplate(this, *args)

fun Template.booleanTemplate(args: List<*>): BooleanTemplate =
    Expressions.booleanTemplate(this, args)

// Operation

inline fun <reified T: Any> Operator.simpleOperation(vararg args: Expression<*>): SimpleOperation<T> =
    Expressions.simpleOperation(T::class.java, this, *args)

inline fun <reified T: Any> Operator.dslOperation(vararg args: Expression<*>): DslOperation<T> =
    Expressions.dslOperation(T::class.java, this, *args)

fun Operator.booleanOperation(vararg args: Expression<*>): BooleanOperation =
    Expressions.booleanOperation(this, *args)


inline fun <reified T: Comparable<*>> Operator.comparableOperation(vararg args: Expression<*>): ComparableOperation<T> =
    Expressions.comparableOperation(T::class.java, this, *args)

inline fun <reified T: Comparable<*>> Operator.dateOperation(vararg args: Expression<*>): DateOperation<T> =
    Expressions.dateOperation(T::class.java, this, *args)

inline fun <reified T: Comparable<*>> Operator.timeOperation(vararg args: Expression<*>): TimeOperation<T> =
    Expressions.timeOperation(T::class.java, this, *args)

inline fun <reified T> Operator.numberOperation(vararg args: Expression<*>): NumberOperation<T> where T: Number, T: Comparable<*> =
    Expressions.numberOperation(T::class.java, this, *args)

fun Operator.stringOperation(vararg args: Expression<*>): StringOperation =
    Expressions.stringOperation(this, *args)

// SimplePath

inline fun <reified T: Any> simplePathOf(variable: String): SimplePath<T> =
    Expressions.simplePath(T::class.java, variable)

inline fun <reified T: Any> simplePathOf(parent: Path<*>, variable: String): SimplePath<T> =
    Expressions.simplePath(T::class.java, parent, variable)

inline fun <reified T: Any> simplePathOf(metadata: PathMetadata): SimplePath<T> =
    Expressions.simplePath(T::class.java, metadata)

// DslPath

inline fun <reified T: Any> dslPathOf(variable: String): DslPath<T> =
    Expressions.dslPath(T::class.java, variable)

inline fun <reified T: Any> dslPathOf(parent: Path<*>, property: String): DslPath<T> =
    Expressions.dslPath(T::class.java, parent, property)

inline fun <reified T: Any> dslPathOf(metadata: PathMetadata): DslPath<T> =
    Expressions.dslPath(T::class.java, metadata)

// ComparablePath

inline fun <reified T: Comparable<*>> comparablePathOf(variable: String): ComparablePath<T> =
    Expressions.comparablePath(T::class.java, variable)

inline fun <reified T: Comparable<*>> comparablePathOf(parent: Path<*>, property: String): ComparablePath<T> =
    Expressions.comparablePath(T::class.java, parent, property)

inline fun <reified T: Comparable<*>> comparablePathOf(metadata: PathMetadata): ComparablePath<T> =
    Expressions.comparablePath(T::class.java, metadata)

// ComparableEntityPath

inline fun <reified T: Comparable<*>> comparableEntityPathOf(variable: String): ComparableEntityPath<T> =
    Expressions.comparableEntityPath(T::class.java, variable)

inline fun <reified T: Comparable<*>> comparableEntityPathOf(
    parent: Path<*>,
    property: String,
): ComparableEntityPath<T> =
    Expressions.comparableEntityPath(T::class.java, parent, property)

inline fun <reified T: Comparable<*>> comparableEntityPathOf(metadata: PathMetadata): ComparableEntityPath<T> =
    Expressions.comparableEntityPath(T::class.java, metadata)

// DatePath

inline fun <reified T: Comparable<*>> datePathOf(variable: String): DatePath<T> =
    Expressions.datePath(T::class.java, variable)

inline fun <reified T: Comparable<*>> datePathOf(parent: Path<*>, property: String): DatePath<T> =
    Expressions.datePath(T::class.java, parent, property)

inline fun <reified T: Comparable<*>> datePathOf(metadata: PathMetadata): DatePath<T> =
    Expressions.datePath(T::class.java, metadata)

// DateTimePath

inline fun <reified T: Comparable<*>> dateTimePathOf(variable: String): DateTimePath<T> =
    Expressions.dateTimePath(T::class.java, variable)

inline fun <reified T: Comparable<*>> dateTimePathOf(parent: Path<*>, property: String): DateTimePath<T> =
    Expressions.dateTimePath(T::class.java, parent, property)

inline fun <reified T: Comparable<*>> dateTimePathOf(metadata: PathMetadata): DateTimePath<T> =
    Expressions.dateTimePath(T::class.java, metadata)

// TimePath

inline fun <reified T: Comparable<*>> timePathOf(variable: String): TimePath<T> =
    Expressions.timePath(T::class.java, variable)

inline fun <reified T: Comparable<*>> timePathOf(parent: Path<*>, property: String): TimePath<T> =
    Expressions.timePath(T::class.java, parent, property)

inline fun <reified T: Comparable<*>> timePathOf(metadata: PathMetadata): TimePath<T> =
    Expressions.timePath(T::class.java, metadata)

// NumberPath

inline fun <reified T> numberPathOf(variable: String): NumberPath<T> where T: Number, T: Comparable<*> =
    Expressions.numberPath(T::class.java, variable)

inline fun <reified T> numberPathOf(
    parent: Path<*>,
    property: String,
): NumberPath<T> where T: Number, T: Comparable<*> =
    Expressions.numberPath(T::class.java, parent, property)

inline fun <reified T> numberPathOf(metadata: PathMetadata): NumberPath<T> where T: Number, T: Comparable<*> =
    Expressions.numberPath(T::class.java, metadata)

// StringPath

fun stringPathOf(variable: String): StringPath =
    Expressions.stringPath(variable)

fun simplePathOf(parent: Path<*>, variable: String): StringPath =
    Expressions.stringPath(parent, variable)

fun simplePathOf(metadata: PathMetadata): StringPath =
    Expressions.stringPath(metadata)

// BooleanPath

fun booleanPathOf(variable: String): BooleanPath =
    Expressions.booleanPath(variable)

fun booleanPathOf(parent: Path<*>, variable: String): BooleanPath =
    Expressions.booleanPath(parent, variable)

fun booleanPathOf(metadata: PathMetadata): BooleanPath =
    Expressions.booleanPath(metadata)

// Expression List

@JvmName("simpleExpressionListOfTuple")
fun simpleExpressionListOf(vararg exprs: SimpleExpression<*>): SimpleExpression<Tuple> =
    Expressions.list(*exprs)

inline fun <reified T: Any> simpleExpressionListOf(vararg exprs: SimpleExpression<*>): SimpleExpression<T> =
    Expressions.list(T::class.java, *exprs)

@JvmName("expressionListOfTuple")
fun expressionListOf(vararg exprs: Expression<*>): Expression<Tuple> =
    Expressions.list(*exprs)

inline fun <reified T: Any> expressionListOf(vararg exprs: Expression<*>): Expression<T> =
    Expressions.list(T::class.java, *exprs)

inline fun <reified T: Any> expressionListOf(exprs: List<Expression<*>>): Expression<T> =
    ExpressionUtils.list(T::class.java, exprs)

// Expression Set

inline fun <reified T: Any> simpleExpressionSetOf(vararg exprs: SimpleExpression<*>): SimpleExpression<T> =
    Expressions.set(T::class.java, *exprs)

@JvmName("expressionSetOfTuple")
fun expressionSetOf(vararg exprs: Expression<*>): Expression<Tuple> =
    Expressions.set(*exprs)

inline fun <reified T: Any> expressionSetOf(vararg exprs: Expression<*>): Expression<T> =
    Expressions.set(T::class.java, *exprs)

inline fun <reified T: Any> nullExpressionOf(): NullExpression<T> =
    Expressions.nullExpression(T::class.java)

fun <T: Any> Path<T>.nullExpression(): NullExpression<T> =
    Expressions.nullExpression(this)

// Enum

inline fun <reified T: Enum<T>> Operator.enumOperation(vararg args: Expression<*>): EnumOperation<T> =
    Expressions.enumOperation(T::class.java, this, *args)

// EnumPath

inline fun <reified T: Enum<T>> enumPathOf(variable: String): EnumPath<T> =
    Expressions.enumPath(T::class.java, variable)

inline fun <reified T: Enum<T>> enumPathOf(parent: Path<*>, property: String): EnumPath<T> =
    Expressions.enumPath(T::class.java, parent, property)

inline fun <reified T: Enum<T>> enumPathOf(metadata: PathMetadata): EnumPath<T> =
    Expressions.enumPath(T::class.java, metadata)

// CollectionExpression

inline fun <reified T: Any> Operator.collectionOperation(vararg args: Expression<*>): CollectionExpression<MutableCollection<T>, T> =
    Expressions.collectionOperation(T::class.java, this, *args)

inline fun <reified E: Any, reified Q: SimpleExpression<in E>> collectionPathOf(metadata: PathMetadata): CollectionPath<E, Q> =
    Expressions.collectionPath(E::class.java, Q::class.java, metadata)

inline fun <reified E: Any, reified Q: SimpleExpression<in E>> listPathOf(metadata: PathMetadata): ListPath<E, Q> =
    Expressions.listPath(E::class.java, Q::class.java, metadata)

inline fun <reified E: Any, reified Q: SimpleExpression<in E>> setPathOf(metadata: PathMetadata): SetPath<E, Q> =
    Expressions.setPath(E::class.java, Q::class.java, metadata)

inline fun <reified K: Any, reified V: Any, reified E: SimpleExpression<in V>> mapPathOf(metadata: PathMetadata): MapPath<K, V, E> =
    Expressions.mapPath(K::class.java, V::class.java, E::class.java, metadata)

// ArrayPath

inline fun <reified A, E> arrayPathOf(variable: String): ArrayPath<A, E> =
    Expressions.arrayPath(A::class.java, variable)

inline fun <reified A, E> arrayPathOf(parent: Path<*>, property: String): ArrayPath<A, E> =
    Expressions.arrayPath(A::class.java, parent, property)

inline fun <reified A, E> arrayPathOf(metadata: PathMetadata): ArrayPath<A, E> =
    Expressions.arrayPath(A::class.java, metadata)

fun Expression<Boolean>.asBoolean(): BooleanExpression =
    Expressions.asBoolean(this)

fun booleanExpressionOf(value: Boolean): BooleanExpression =
    Expressions.asBoolean(value)

fun <T: Comparable<T>> Expression<T>.asComparable(): ComparableExpression<T> =
    Expressions.asComparable(this)

fun <T: Comparable<T>> comparableExpressionOf(value: T): ComparableExpression<T> =
    Expressions.asComparable(value)

fun <T: Comparable<T>> Expression<T>.asDate(): DateExpression<T> =
    Expressions.asDate(this)

fun <T: Comparable<T>> dateExpressionOf(value: T): DateExpression<T> =
    Expressions.asDate(value)

fun <T: Comparable<T>> Expression<T>.asDateTime(): DateTimeExpression<T> =
    Expressions.asDateTime(this)

fun <T: Comparable<T>> dateTimeExpressionOf(value: T): DateTimeExpression<T> =
    Expressions.asDateTime(value)

fun <T: Comparable<T>> Expression<T>.asTime(): TimeExpression<T> =
    Expressions.asTime(this)

fun <T: Comparable<T>> timeExpressionOf(value: T): TimeExpression<T> =
    Expressions.asTime(value)

fun <T: Enum<T>> Expression<T>.asEnum(): EnumExpression<T> =
    Expressions.asEnum(this)

fun <T: Enum<T>> enumExpressionOf(value: T): EnumExpression<T> =
    Expressions.asEnum(value)

fun <T> Expression<T>.asNumber(): NumberExpression<T> where T: Number, T: Comparable<T> =
    Expressions.asNumber(this)

fun <T> numberExpressionOf(value: T): NumberExpression<T> where T: Number, T: Comparable<T> =
    Expressions.asNumber(value)

fun Expression<String>.asString(): StringExpression = Expressions.asString(this)
fun stringExpressionOf(value: String): StringExpression = Expressions.asString(value)

fun <T> T.asSimple(): SimpleExpression<T> = Expressions.asSimple(this)
fun <T> Expression<T>.asSimple(): SimpleExpression<T> = Expressions.asSimple(this)
