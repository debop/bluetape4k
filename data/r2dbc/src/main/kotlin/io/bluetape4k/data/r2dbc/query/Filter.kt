package io.bluetape4k.data.r2dbc.query

sealed class Filter {

    abstract fun countLeaves(): Int

    class Group(
        val operator: String = "and",
        val filters: MutableList<Filter> = mutableListOf(),
    ): Filter() {

        override fun countLeaves(): Int {
            fun countLeaves(conditions: Group): Int =
                conditions.filters.fold(0) { count, filter -> count + filter.countLeaves() }
            return countLeaves(this)
        }
    }

    class Where(val where: String): Filter() {
        override fun countLeaves(): Int = 1
    }
}
