package io.bluetape4k.workshop.r2dbc.service

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.r2dbc.AbstractWebfluxR2dbcApplicationTest
import io.bluetape4k.workshop.r2dbc.domain.toDto
import io.bluetape4k.workshop.r2dbc.repository.UserRepository
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserServiceTest(
    @Autowired private val connectionFactory: ConnectionFactory,
    @Autowired private val service: UserService,
    @Autowired private val repository: UserRepository,
): AbstractWebfluxR2dbcApplicationTest() {

    companion object: KLogging()

    @Test
    @Order(1)
    fun `context loading`() {
        connectionFactory.shouldNotBeNull()
        service.shouldNotBeNull()
        repository.shouldNotBeNull()
    }

    @Test
    @Order(2)
    fun `find all users`() = runTest {
        val users = service.findAll().toList()
        users.forEach {
            log.debug { it }
        }
        users.shouldNotBeEmpty()
    }

    @Test
    @Order(3)
    fun `find user by id`() = runTest {
        val expected = service.findAll().toList().random()

        val actual = service.findById(expected.id!!)
        log.debug { actual }
        actual.shouldNotBeNull() shouldBeEqualTo expected
    }

    @Test
    @Order(4)
    fun `find user by invalid id`() = runTest {
        val actual = service.findById(-1)
        actual.shouldBeNull()
    }

    @Test
    @Order(5)
    fun `find user by email`() = runTest {
        val expected = service.findAll().toList().random()

        val actual = service.findByEmail(expected.email).single()
        log.debug { actual }
        actual.shouldNotBeNull() shouldBeEqualTo expected
    }

    @Test
    @Order(6)
    fun `find user by invalid email`() = runTest {
        val notFounds = service.findByEmail("not-exists@example.com").toList()
        notFounds.shouldBeEmpty()
    }

    @Test
    @Order(7)
    fun `add new user`() = runTest {
        val newUser = createUserDTO()
        val savedUser = service.addUser(newUser)

        savedUser.shouldNotBeNull()
        savedUser.id.shouldNotBeNull()
        savedUser.toDto() shouldBeEqualTo newUser
    }

    @Test
    @Order(8)
    fun `update existing user`() = runTest {
        val user = service.findAll().toList().random()

        val updated = service.updateUser(user.id!!, user.copy(avatar = "updated-avatar.jpg").toDto())
        log.debug { "Updated=$updated" }
        updated.shouldNotBeNull()
        updated.id shouldBeEqualTo user.id
        updated.avatar shouldNotBeEqualTo user.avatar

        val saved = service.findByEmail(user.email).first()
        saved.shouldNotBeNull()
        saved.avatar shouldBeEqualTo updated.avatar
    }

    @Test
    @Order(9)
    fun `update non existing user`() = runTest {
        val nonExists = createUserDTO()
        val actual = service.updateUser(-1, nonExists)
        actual.shouldBeNull()
    }

    @Test
    @Order(10)
    fun `delete existing user`() = runTest {
        val user = createUserDTO()
        val saved = service.addUser(user)
        saved.shouldNotBeNull()

        service.deleteUser(saved.id!!).shouldBeTrue()
        service.findById(saved.id!!).shouldBeNull()
    }

    @Test
    @Order(11)
    fun `delete non-existing user`() = runTest {
        service.deleteUser(-1).shouldBeFalse()
    }
}
