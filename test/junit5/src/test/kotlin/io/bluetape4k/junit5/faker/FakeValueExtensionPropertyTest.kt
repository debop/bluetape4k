package io.bluetape4k.junit5.faker

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.TestInstance

@FakeValueTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FakeValueExtensionPropertyTest {

    companion object : KLogging() {
        const val TEST_COUNT = 10
    }

    @RepeatedTest(TEST_COUNT)
    fun `inject fake value by provider`(
        @FakeValue(provider = FakeValueProvider.Name.FullName) fullName: String,
        @FakeValue(provider = FakeValueProvider.Name.FirstName) firstName: String,
        @FakeValue(provider = FakeValueProvider.Name.LastName) lastName: String,
    ) {
        fullName.shouldNotBeEmpty()
        firstName.shouldNotBeEmpty()
        lastName.shouldNotBeEmpty()

        log.debug { "fullName=$fullName" }
        log.debug { "firstName=$firstName" }
        log.debug { "lastName=$lastName" }
    }

    @RepeatedTest(TEST_COUNT)
    fun `inject fake value by random provider`(
        @FakeValue(provider = "number.randomDigit") intValue: Int,
        @FakeValue(provider = "number.randomDigitNotZero") nonZero: Int,
        @FakeValue(provider = "random.nextLong") longValue: Long,
        @FakeValue(provider = "random.nextDouble") doubleValue: Double,
    ) {
        nonZero shouldBeGreaterThan 0
        log.debug { "int value = $intValue" }
        log.debug { "long value = $longValue" }
        log.debug { "double value = $doubleValue" }
    }

    @RepeatedTest(TEST_COUNT)
    fun `inject fake credit card`(
        @FakeValue(provider = "finance.creditCard") creditCard: String,
        @FakeValue(provider = "finance.bic") bic: String,
    ) {
        creditCard.shouldNotBeEmpty()
        creditCard.length shouldBeGreaterOrEqualTo 8
        bic.shouldNotBeEmpty()

        log.debug { "creditCard=$creditCard" }
        log.debug { "bic=$bic" }
    }

    @RepeatedTest(TEST_COUNT)
    fun `inject multiple usernames`(
        @FakeValue(provider = FakeValueProvider.Name.Username, type = String::class, size = 20) usernames: List<String>,
    ) {
        usernames.size shouldBeEqualTo 20
        usernames.all { it.isNotBlank() }.shouldBeTrue()
    }
}
