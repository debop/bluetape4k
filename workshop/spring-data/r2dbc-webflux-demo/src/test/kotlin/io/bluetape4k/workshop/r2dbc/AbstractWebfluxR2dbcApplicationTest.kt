package io.bluetape4k.workshop.r2dbc

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.r2dbc.domain.User
import io.bluetape4k.workshop.r2dbc.domain.UserDTO
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractWebfluxR2dbcApplicationTest {

    companion object: KLogging() {
        @JvmStatic
        val faker = Fakers.faker
    }

    protected fun createUser(id: Int? = null): User =
        User(
            name = faker.name().fullName(),
            login = faker.internet().username(),
            email = faker.internet().emailAddress(),
            avatar = faker.avatar().image(),
            id = id
        )

    protected fun createUserDTO(): UserDTO =
        UserDTO(
            name = faker.name().fullName(),
            login = faker.internet().username(),
            email = faker.internet().emailAddress(),
            avatar = faker.avatar().image()
        )
}
