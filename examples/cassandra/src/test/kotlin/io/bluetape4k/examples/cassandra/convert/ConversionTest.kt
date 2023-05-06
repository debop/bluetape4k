package io.bluetape4k.examples.cassandra.convert

import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom
import io.bluetape4k.data.cassandra.data.getList
import io.bluetape4k.examples.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.cassandra.insertSuspending
import io.bluetape4k.spring.cassandra.selectOneSuspending
import io.bluetape4k.spring.cassandra.truncateSuspending
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import java.util.*

@SpringBootTest(classes = [ConversionTestConfiguration::class])
class ConversionTest(
    @Autowired private val operations: ReactiveCassandraOperations,
): AbstractCassandraCoroutineTest("conversion") {

    companion object: KLogging()

    @BeforeEach
    fun setup() = runSuspendTest {
        operations.truncateSuspending<Addressbook>()
    }

    @Test
    fun `context loading`() {
        operations.shouldNotBeNull()
    }

    @Test
    fun `write Addressbook`() = runSuspendTest {
        val addressbook = Addressbook(
            id = "private",
            me = Contact("Debop", "Bae"),
            friends = mutableListOf(Contact("William", "Shin"), Contact("Glenn", "Han"))
        )
        operations.insertSuspending(addressbook)

        val row = operations.selectOneSuspending<Row>(selectFrom("addressbook").all().build())!!

        row.getString("id") shouldBeEqualTo "private"
        row.getString("me")!! shouldContain """"firstname":"Debop""""
        row.getList<String>("friends")!!.size shouldBeEqualTo 2
    }

    @Test
    fun `read Addressbook`() = runSuspendTest {
        val addressbook = Addressbook(
            id = "private",
            me = Contact("Debop", "Bae"),
            friends = mutableListOf(Contact("William", "Shin"), Contact("Glenn", "Han"))
        )
        operations.insertSuspending(addressbook)

        val loaded = operations.selectOneSuspending<Addressbook>(selectFrom("addressbook").all().build())!!

        loaded.me shouldBeEqualTo addressbook.me
        loaded.friends shouldBeEqualTo addressbook.friends
    }

    @Test
    fun `write converted maps and user defined type`() = runSuspendTest {
        val addressbook = Addressbook(
            id = "private",
            me = Contact("Debop", "Bae"),
            friends = mutableListOf(Contact("William", "Shin"), Contact("Glenn", "Han")),
            address = Address("165 Misa", "Hanam", "12914"),
            preferredCurrencies = mutableMapOf(
                1 to Currency.getInstance("USD"),
                2 to Currency.getInstance("KRW")
            )
        )

        operations.insertSuspending(addressbook)

        val loaded = operations.selectOneSuspending<Addressbook>(selectFrom("addressbook").all().build())!!

        loaded.me shouldBeEqualTo addressbook.me
        loaded.friends shouldBeEqualTo addressbook.friends
        loaded.address shouldBeEqualTo addressbook.address
        loaded.preferredCurrencies shouldBeEqualTo addressbook.preferredCurrencies
    }
}
