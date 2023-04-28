package io.bluetape4k.spring.cassandra.domain.schema

import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.cassandra.AbstractCassandraTest
import io.bluetape4k.spring.cassandra.domain.model.AllPossibleTypes
import io.bluetape4k.spring.cassandra.schema.SchemaBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.CassandraOperations

@SpringBootTest(classes = [SchemaBuilderTestConfiguration::class])
class SchemaBuilderTest(
    @Autowired private val operations: CassandraOperations
): AbstractCassandraTest() {

    companion object: KLogging()

    @Test
    fun `generate table schema for entity`() {
        SchemaBuilder.truncate<AllPossibleTypes>(operations)
        SchemaBuilder.potentiallyCreateTableFor<AllPossibleTypes>(operations)
        SchemaBuilder.truncate<AllPossibleTypes>(operations)
    }
}
