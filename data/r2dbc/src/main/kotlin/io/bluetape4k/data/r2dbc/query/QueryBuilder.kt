package io.bluetape4k.data.r2dbc.query

import io.bluetape4k.data.r2dbc.support.toParameter
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.Systemx
import io.r2dbc.spi.Parameters
import kotlin.reflect.KProperty

/**
 * QueryBuilder is a simple SQL query builder to ease building dynamic queries
 * where clauses and parameters may be optional.
 *
 * It is not a type-safe builder, it simply allows fragments of an SQL statement
 * to be added in any order and then builds the statement.
 *
 * The main feature it provides is allowing where clauses to be combined with
 * an operator, taking care of cases where groups end up being empty.
 */
class QueryBuilder {

    companion object: KLogging()

    private val selects = LinkedHashSet<String>()
    private var selectCount: String? = null
    private val params = mutableMapOf<String, Any>()
    private var filters: Filter.Group = Filter.Group()
    private var groupBy: String? = null
    private var having: String? = null
    private var orderBy: String? = null
    private var limit: Int? = null
    private var offset: Int? = null

    fun select(table: String) = apply {
        selects.add(table)
    }

    fun selectCount(selectCount: String) = apply {
        this.selectCount = selectCount
    }

    fun parameter(name: String, value: Any) = apply {
        params[name] = value
    }

    fun parameter(name: String, value: Any?, type: Class<*>) = apply {
        params[name] = value.toParameter(type)
    }

    fun parameter(property: KProperty<*>, value: Any) = apply {
        params[property.name] = value
    }

    fun parameter(property: KProperty<*>, value: Any?, type: Class<*>) = apply {
        params[property.name] = value.toParameter(type)
    }

    fun parameterNull(name: String, type: Class<*>) = apply {
        params[name] = Parameters.`in`(type)
    }

    fun parameterNull(property: KProperty<*>, type: Class<*>) = apply {
        params[property.name] = Parameters.`in`(type)
    }

    fun groupBy(groupBy: String) = apply {
        this.groupBy = groupBy
    }

    fun groupBy(groupBy: KProperty<*>) = apply {
        this.groupBy = groupBy.name
    }


    fun having(having: String) = apply {
        this.having = having
    }

    fun orderBy(orderBy: String) = apply {
        this.orderBy = orderBy
    }

    fun limit(limit: Int) = apply {
        this.limit = limit
    }

    fun offset(offset: Int) = apply {
        this.offset = offset
    }

    fun whereGroup(operator: String = "and", block: FilterBuilder.() -> Unit) {
        require(filters.countLeaves() == 0) { "There must be only one root filters group" }
        filters = Filter.Group(operator)
        block(FilterBuilder(filters))
    }

    inner class FilterBuilder(private val group: Filter.Group) {
        fun where(where: String) {
            group.filters.add(Filter.Where(where))
        }

        fun whereGroup(operator: String = "and", block: FilterBuilder.() -> Unit) {
            val inner = Filter.Group(operator)
            group.filters.add(inner)
            block(FilterBuilder(inner))
        }
    }

    fun build(sb: StringBuilder = StringBuilder(), block: QueryBuilder.() -> Unit): Query {
        block(this)

        selects.joinTo(sb, Systemx.LineSeparator)
        if (filters.countLeaves() != 0) {
            if (selects.isNotEmpty()) sb.appendLine()
            sb.append("where ")
            appendConditions(sb, filters, true)
        }
        groupBy?.run { sb.appendLine().append("group by ").append(groupBy) }
        having?.run { sb.appendLine().append("having ").append(having) }
        orderBy?.run { sb.appendLine().append("order by ").append(orderBy) }
        limit?.run { sb.appendLine().append("limit ").append(limit) }
        offset?.run { sb.appendLine().append("offset ").append(offset) }

        return Query(sb, params).apply {
            log.debug { "query. $this" }
        }
    }

    fun buildCount(sb: StringBuilder = StringBuilder(), block: QueryBuilder.() -> Unit): Query {
        block(this)

        selectCount?.run { sb.append(selectCount) }
        if (filters.countLeaves() != 0) {
            if (selectCount != null) sb.appendLine()
            sb.append("where ")
            appendConditions(sb, filters, true)
        }
        return Query(sb, params).apply {
            log.debug { "count query. $this" }
        }
    }

    private fun appendConditions(sb: StringBuilder, conditions: Filter, root: Boolean) {
        when (conditions) {
            is Filter.Where -> sb.append(conditions.where)
            is Filter.Group -> appendConditions(conditions, root, sb)
        }
    }

    private fun appendConditions(conditions: Filter.Group, root: Boolean, sb: StringBuilder) {
        val filtered = conditions.filters.filter { it.countLeaves() != 0 }
        when (filtered.size) {
            0    -> Unit
            1    -> appendConditions(sb, filtered.first(), false)
            else -> {
                if (!root) sb.appendLine().append("(")
                filtered.forEachIndexed { index, cond ->
                    log.debug { "append condition group index=$index, condition=$cond" }
                    appendConditions(sb, cond, false)
                    if (index != filtered.indices.last) sb.append(" ${conditions.operator} ")
                }

                if (!root) sb.append(")")
            }
        }
    }
}
