package io.bluetape4k.data.r2dbc.query

import io.bluetape4k.data.r2dbc.AbstractR2dbcTest
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class QueryBuilderTest: AbstractR2dbcTest() {

    companion object: KLogging()

    @Test
    fun `select statement 지원`() {
        val query = QueryBuilder().build {
            select("select * from actor")
        }
        query.sql shouldBeEqualTo """
            select * from actor
        """.trimIndent()
    }

    @Test
    fun `select with order by 지원`() {
        val query = QueryBuilder().build {
            select("select * from actor")
            orderBy("actor_id")
        }
        query.sql shouldBeEqualTo """
            select * from actor
            order by actor_id
            """.trimIndent()
    }

    @Test
    fun `select with where`() {
        val query = QueryBuilder().build {
            select("select * from actor")
            whereGroup {
                where("first_name = 'Kate'")
            }
        }
        query.sql shouldBeEqualTo """
            select * from actor
            where first_name = 'Kate'
            """.trimIndent()
    }

    @Test
    fun `select with where with param`() {
        val query = QueryBuilder().build {
            select("select * from actor")
            whereGroup {
                where("first_name = :first_name")
                parameter("first_name", "Kate")
            }
        }
        query.sql shouldBeEqualTo """
            select * from actor
            where first_name = :first_name
            """.trimIndent()

        query.parameters.keys shouldBeEqualTo setOf("first_name")
    }
}
