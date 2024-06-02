package io.bluetape4k.workshop.graphql.dgs.datafetchers

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import com.netflix.graphql.dgs.client.jsonTypeRef
import graphql.schema.Coercing
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.graphql.dgs.AbstractDgsTest
import io.bluetape4k.workshop.graphql.dgs.generated.client.ReviewsGraphQLQuery
import io.bluetape4k.workshop.graphql.dgs.generated.client.ReviewsProjectionRoot
import io.bluetape4k.workshop.graphql.dgs.generated.types.Review
import io.bluetape4k.workshop.graphql.dgs.scalars.DateRange
import io.bluetape4k.workshop.graphql.dgs.scalars.DateRangeScalar
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

class ReviewDataFetcherTest(
    @Autowired private val dgsQueryExecutor: DgsQueryExecutor,
): AbstractDgsTest() {

    companion object: KLogging()

    @Suppress("DEPRECATION")
    @Test
    fun `get reviews by date range`() {
        val scalars: Map<Class<*>, Coercing<*, *>> = mapOf(DateRange::class.java to DateRangeScalar())

        val dateRange = DateRange(LocalDate.of(2020, 1, 1), LocalDate.now())
        val request = GraphQLQueryRequest(
            ReviewsGraphQLQuery.newRequest().dateRange(dateRange).build(),
            ReviewsProjectionRoot().username().submittedDate().starScore(),
            scalars,
        )

        val input = scalars.getValue(DateRange::class.java).valueToLiteral(dateRange)
        log.debug { "input=$input" }

        val query = request.serialize()
        log.debug { "query=$query" }

        val reviews: List<Review> = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query,
            "data.reviews",
            jsonTypeRef<List<Review>>()
        )
        reviews.forEach { log.debug { it } }
        reviews.shouldNotBeEmpty()
    }
}
