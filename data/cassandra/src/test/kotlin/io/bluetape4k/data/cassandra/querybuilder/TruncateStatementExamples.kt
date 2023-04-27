package io.bluetape4k.data.cassandra.querybuilder

import com.datastax.oss.driver.api.querybuilder.QueryBuilder.truncate
import io.bluetape4k.data.cassandra.toCqlIdentifier
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class TruncateStatementExamples {

    companion object: KLogging()

    @Test
    fun `generate truncate for table name`() {
        truncate("foo").asCql() shouldBeEqualTo "TRUNCATE foo"
        truncate("ks", "foo").asCql() shouldBeEqualTo "TRUNCATE ks.foo"
    }

    @Test
    fun `generate truncate for table CqlIdentifier`() {
        truncate("foo".toCqlIdentifier()).asCql() shouldBeEqualTo "TRUNCATE foo"
        truncate("ks".toCqlIdentifier(), "foo".toCqlIdentifier()).asCql() shouldBeEqualTo "TRUNCATE ks.foo"
    }

    @Test
    fun `should throw if call build with arguments`() {
        assertFailsWith<UnsupportedOperationException> {
            truncate("foo").build("arg1")
        }

        assertFailsWith<UnsupportedOperationException> {
            truncate("foo").build(mapOf("k" to 1))
        }
    }
}
