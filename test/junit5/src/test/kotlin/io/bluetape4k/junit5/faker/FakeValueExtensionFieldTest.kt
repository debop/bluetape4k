package io.bluetape4k.junit5.faker

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.TestInstance

@FakeValueTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class FakeValueExtensionFieldTest {
    companion object : KLogging() {
        const val TEST_COUNT = 10
    }

    @FakeValue(provider = FakeValueProvider.Name.Title)
    private lateinit var title: String

    @FakeValue(provider = FakeValueProvider.Name.Username)
    private lateinit var username: String

    @RepeatedTest(TEST_COUNT)
    fun `inject from name provider`() {
        title.shouldNotBeEmpty()
        username.shouldNotBeEmpty()
    }


    @FakeValue(provider = FakeValueProvider.Name.Username, type = String::class, size = 20)
    private lateinit var usernames: List<String>

    @RepeatedTest(TEST_COUNT)
    fun `inject string list`() {
        usernames.size shouldBeEqualTo 20
        usernames.all { it.isNotBlank() }.shouldBeTrue()
    }
}
