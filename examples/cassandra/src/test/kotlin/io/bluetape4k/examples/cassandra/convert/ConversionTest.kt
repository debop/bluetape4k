package io.bluetape4k.examples.cassandra.convert

import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom
import io.bluetape4k.cassandra.data.getList
import io.bluetape4k.examples.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.cassandra.selectOne
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.truncate
import java.util.*

@SpringBootTest(classes = [ConversionTestConfiguration::class])
class ConversionTest(
    @Autowired private val operations: ReactiveCassandraOperations,
): AbstractCassandraCoroutineTest("conversion") {

    companion object: KLogging()

    private fun newContact(): Contact =
        Contact(faker.name().firstName(), faker.name().lastName())

    private fun newAddressbook(): Addressbook =
        Addressbook(
            id = "private",
            me = Contact(faker.name().firstName(), faker.name().lastName()),
            friends = mutableListOf(newContact(), newContact())
        )

    @BeforeEach
    fun setup() = runSuspendTest {
        operations.truncate<Addressbook>().awaitSingleOrNull()
    }

    @Test
    fun `context loading`() {
        operations.shouldNotBeNull()
    }

    @Test
    fun `write Addressbook`() = runSuspendWithIO {
        val addressbook = Addressbook(
            id = "private",
            me = Contact("Debop", "Bae"),
            friends = mutableListOf(newContact(), newContact())
        )
        operations.insert(addressbook).awaitSingle()

        val row = operations.selectOne<Row>(selectFrom("addressbook").all().build()).awaitSingle()

        row.getString("id") shouldBeEqualTo "private"
        row.getString("me")!! shouldContain """"firstname":"Debop""""
        row.getList<String>("friends")!!.size shouldBeEqualTo 2
    }

    @Test
    fun `read Addressbook`() = runSuspendWithIO {
        val addressbook = Addressbook(
            id = "private",
            me = Contact("Debop", "Bae"),
            friends = mutableListOf(newContact(), newContact())
        )
        operations.insert(addressbook).awaitSingle()

        val loaded = operations.selectOne<Addressbook>(selectFrom("addressbook").all().build()).awaitSingle()

        loaded.me shouldBeEqualTo addressbook.me
        loaded.friends shouldBeEqualTo addressbook.friends
    }

    @Test
    fun `write converted maps and user defined type`() = runSuspendWithIO {
        val addressbook = Addressbook(
            id = "private",
            me = Contact("Debop", "Bae"),
            friends = mutableListOf(newContact(), newContact()),
            address = Address("165 Misa", "Hanam", "12914"),
            preferredCurrencies = mutableMapOf(
                1 to Currency.getInstance("USD"),
                2 to Currency.getInstance("KRW")
            )
        )

        operations.insert(addressbook).awaitSingle()

        val loaded = operations.selectOne<Addressbook>(selectFrom("addressbook").all().build()).awaitSingle()

        loaded.me shouldBeEqualTo addressbook.me
        loaded.friends shouldBeEqualTo addressbook.friends
        loaded.address shouldBeEqualTo addressbook.address
        loaded.preferredCurrencies shouldBeEqualTo addressbook.preferredCurrencies
    }
}
