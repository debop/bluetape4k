package io.bluetape4k.workshop.security.server

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.security.server.application.domain.Customer
import io.bluetape4k.workshop.security.server.application.security.JwtService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {

    companion object: KLogging() {
        @JvmStatic
        protected val faker = Fakers.faker
    }

    @Autowired
    protected val client: WebTestClient = uninitialized()

    @Autowired
    protected val jwtService: JwtService = uninitialized()

    @Value("\${app.first_user.username}")
    private lateinit var firstUsername: String

    @Value("\${app.first_user.password}")
    private lateinit var firstPassword: String

    protected fun accessToken(email: String = firstUsername, role: String = "ROLE_USER"): String {
        return "Bearer " + jwtService.accessToken(email, 60 * 1000, arrayOf(role))
    }

    protected fun adminAccessToken() = accessToken(role = "ROLE_ADMIN")

    protected fun randomCustomer(): Customer {
        return Customer(
            id = Fakers.randomUuid().encodeBase62(),
            email = faker.internet().username() + "@example.com",
            password = Fakers.randomUuid().encodeBase62()
        )
    }
}
