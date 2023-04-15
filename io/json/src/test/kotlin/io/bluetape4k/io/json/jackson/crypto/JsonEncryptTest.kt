package io.bluetape4k.io.json.jackson.crypto

import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.io.crypto.encrypt.AES
import io.bluetape4k.io.crypto.encrypt.RC4
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.io.json.jackson.prettyWriteAsString
import io.bluetape4k.io.json.jackson.writeAsString
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

@RandomizedTest
class JsonEncryptTest {

    companion object: KLogging() {
        private const val REPEAT_COUNT = 5
    }

    data class User(
        val username: String,

        @field:JsonEncrypt(encryptor = AES::class)
        val password: String,

        @get:JsonEncrypt(encryptor = RC4::class)
        val mobile: String,
    )

    private val mapper = Jackson.defaultJsonMapper

    @Test
    fun `encrypt string property`() {
        val user = User("debop", "mypassword", "010-8955-0581")
        log.debug { mapper.prettyWriteAsString(user) }

        val encrypted = mapper.writeAsString(user)!!
        encrypted shouldNotContain "mypassword"
        encrypted shouldNotContain "010-8955-5081"
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `encrypt json property`(@RandomValue user: User) {
        val encrypted = mapper.writeValueAsString(user)
        log.debug { "encrypted=$encrypted" }

        val actual = mapper.readValue<User>(encrypted)
        actual shouldBeEqualTo user
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `encrypt json property in list`(@RandomValue(type = User::class, size = 10) users: List<User>) {
        val encrypted = mapper.writeValueAsString(users)
        log.debug { "encrypted=$encrypted" }

        val actuals = mapper.readValue<List<User>>(encrypted)
        actuals shouldBeEqualTo users
    }
}
